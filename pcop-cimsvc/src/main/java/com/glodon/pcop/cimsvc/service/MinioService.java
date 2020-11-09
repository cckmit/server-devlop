package com.glodon.pcop.cimsvc.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.common.constant.FileImportTypeEnum;
import com.glodon.pcop.cim.common.model.FileUploadStatusBean;
import com.glodon.pcop.cim.common.model.minio.DeleteFileInputBean;
import com.glodon.pcop.cim.common.model.minio.DeleteFileOutputBean;
import com.glodon.pcop.cim.common.service.MyMinioClient;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.common.util.ExcelFileReader;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObject;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectValue;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceUserException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimapi.exception.MinioClientException;
import com.glodon.pcop.cimsvc.config.properties.MimeTypeConfig;
import com.glodon.pcop.cimsvc.model.BatchDownloadBean;
import com.glodon.pcop.cimsvc.model.v2.mapping.FileStructBean;
import com.glodon.pcop.cimsvc.service.v2.FileDataImportService;
import com.glodon.pcop.cimsvc.service.v2.OfficeConverterService;
import com.glodon.pcop.cimsvc.util.DateUtil;
import com.glodon.pcop.core.models.IdResult;
import com.glodon.pcop.core.tenancy.context.TenantContext;
import com.glodon.pcop.jobapi.JobResponse;
import com.glodon.pcop.jobapi.dto.JobParmDTO;
import com.glodon.pcop.jobapi.dto.JobPropsDTO;
import com.glodon.pcop.jobapi.type.JobStatusEnum;
import com.glodon.pcop.jobclt.client.JobInfoClient;
import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.Result;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import io.minio.messages.DeleteError;
import io.minio.messages.Item;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.MimeTypeUtils;
import org.xmlpull.v1.XmlPullParserException;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author yuanjk
 */
@Component
public class MinioService {
	private static Logger log = LoggerFactory.getLogger(MinioService.class);

	public static final String CONTENT_DISPOSITION = "Content-Disposition";
	public static final String UTF8 = "UTF-8";
	private static final String FILE_DOWNLOAD_FAILED = "file download failed!";
	// public static final long MAX_BIM_FILE_SIZE = 100 * 1024 * 1024;//100M

	@Value("${my.minio.url}")
	private String url;

	@Value("${my.minio.shareUrl}")
	private String shareUrl;

	@Value("${my.minio.user-name}")
	private String userName;

	@Value("${my.minio.password}")
	private String pwd;

	@Value("${my.minio.bucket}")
	private String bucket;

	@Autowired
	private ObjectMapper objectMapper;

	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	@Autowired
	private JobInfoClient jobInfoClient;
	@Autowired
	private TenantContext tenantContext;

	@Autowired
	private MimeTypeConfig mimeTypeConfig;

	private String fileDownload = "fileDownload";

	private String fileDownloadPrefix = "fileDownloadPrefix";

	@Autowired
	private ServletContext context;

	@Autowired
	private OfficeConverterService officeConverterService;

	@Autowired
	private FileDataImportService dataImportService;

	private MinioClient minioClient = null;

	public String getUrl() {
		return url;
	}

	public String getShareUrl() {
		return shareUrl;
	}


	@PostConstruct
	public void MinioClientInit() {
		try {
			minioClient = new MinioClient(url, userName, pwd);
		} catch (Exception e) {
			log.error("Minio client initiation failed! url={}, userName={}, pass_word={}", url, userName, pwd);
			e.printStackTrace();
		}
	}

	public MinioService() {
		super();
	}

	public MinioService(MinioClient minioClient, String bucket) {
		super();
		this.bucket = bucket;
		this.minioClient = minioClient;
	}

	/**
	 * 上传本地文件到minio
	 *
	 * @param objectName 文件对象名称
	 * @param filePath   本地文件存放路径
	 */
	public void fileUpload(String objectName, String filePath) {
		try {
			String contentType = URLConnection.guessContentTypeFromName(objectName);
			if (contentType != null) {
				minioClient.putObject(bucket, objectName, filePath, contentType);

			} else {
				minioClient.putObject(bucket, objectName, filePath);
			}
		} catch (Exception e) {
			log.error("上传文件失败：{}", filePath);
			e.printStackTrace();
		}
	}

	public void fileUpload(String objectName, String filePath, String contentType) {
		try {
			minioClient.putObject(bucket, objectName, filePath, contentType);
		} catch (Exception e) {
			log.error("上传文件失败：{}", filePath);
			e.printStackTrace();
		}
	}

	/**
	 * minio文件下载
	 *
	 * @param objectName 文件对象名称
	 * @param filePath   下载文件存放路径
	 */
	public void fileDownload(String objectName, String filePath) {
		ObjectStat objectStat;
		try {
			objectStat = minioClient.statObject(bucket, objectName);

			minioClient.getObject(bucket, objectName, filePath);
		} catch (Exception e) {
			log.error("bucket={}, objectName={}不存在，文件下载失败", bucket, objectName);
			e.printStackTrace();
		}

	}

	public FileUploadStatusBean uploader(String bucket, String objectName, InputStream inputStream,
										 Boolean isOverwrite) {
		FileUploadStatusBean statusBean = null;
		try {
			MinioClient minioClient = new MinioClient(url, userName, pwd);
			statusBean = MyMinioClient.uploader(minioClient, inputStream, bucket, objectName, isOverwrite);
		} catch (Exception e) {
			log.error("file upload failed!", e);
			e.printStackTrace();
		}
		return statusBean;
	}

	/**
	 * 部分office格式文件转PDF格式文件预览
	 *
	 * @param bucket
	 * @param objectName
	 * @param response
	 */
	public void officePreview(String bucket, String objectName, HttpServletResponse response) {
		try {
			if (StringUtils.isBlank(objectName)) {
				response.setStatus(HttpStatus.SC_NOT_FOUND);
				log.error("file name is mandatory");
				return;
			}
			String outputFileName = objectName.substring(0, objectName.lastIndexOf('.')) + ".pdf";

			ObjectStat objectStat = minioClient.statObject(bucket, objectName);
			if (objectStat == null) {
				response.setStatus(HttpStatus.SC_NOT_FOUND);
				log.error("file not found");
				return;
			} else {
				Long fileSize = objectStat.length();
				if (fileSize > CimConstants.MAX_OFFICE_FILE_SIZE) {
					response.setStatus(HttpStatus.SC_SEE_OTHER);
					log.error("file is too large, cannot preview online");
					return;
				}
			}

			InputStream inputStream = null;
			try {
				inputStream = minioDownloader(bucket, objectName);
				if (inputStream != null) {
					response.setContentType(MediaType.APPLICATION_PDF_VALUE);
					response.setHeader(CONTENT_DISPOSITION, "inline;filename=" + URLEncoder.encode(outputFileName,
							UTF8));
					// Copy the stream to the response's output stream.
					try (OutputStream os = response.getOutputStream()) {
						officeConverterService.officeToPdfConverter(inputStream, os, objectName);
						response.flushBuffer();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					response.setStatus(HttpStatus.SC_NOT_FOUND);
				}
			} finally {
				if (inputStream != null) {
					inputStream.close();
				}
			}
		} catch (Exception e) {
			response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			log.error(FILE_DOWNLOAD_FAILED, e);
			e.printStackTrace();
		}
	}


	public void officePreviewV2(String bucket, String objectName, HttpServletResponse response) {
		try {
			if (StringUtils.isBlank(objectName)) {
				response.setStatus(HttpStatus.SC_NOT_FOUND);
				log.error("file name is mandatory");
				return;
			}
			String outputFileName = objectName.substring(0, objectName.lastIndexOf('.')) + ".pdf";

			ObjectStat objectStat;
			try {
				objectStat = minioClient.statObject(bucket, objectName);
			} catch (Exception e) {
				log.error("minio object not found", e);
				throw new MinioClientException(EnumWrapper.CodeAndMsg.E05060004);
			}
			if (objectStat == null) {
				response.setStatus(HttpStatus.SC_NOT_FOUND);
				log.error("file not found");
				return;
			} else {
				Long fileSize = objectStat.length();
				if (fileSize > CimConstants.MAX_OFFICE_FILE_SIZE) {
					response.setStatus(HttpStatus.SC_SEE_OTHER);
					log.error("file is too large, cannot preview online");
					return;
				}
			}

			InputStream inputStream = null;
			File pdfFile = null;
			try {
				try {
					inputStream = minioClient.getObject(bucket, outputFileName);
				} catch (Exception e) {
					log.info("file not found, should be convertered");
				}
				if (inputStream == null) {
					log.info("office file should be convertered: [bucket={}, objectName={}]", bucket, objectName);
					pdfFile = officeConverterService.minioFileConverter(minioClient, bucket, objectName,
							outputFileName);
					inputStream = new FileInputStream(pdfFile);
				} else {
					log.info("===pdf file already exists");
				}

				response.setContentType(MediaType.APPLICATION_PDF_VALUE);
				response.setHeader(CONTENT_DISPOSITION, "inline;filename=" + URLEncoder.encode(outputFileName, UTF8));
				IOUtils.copy(inputStream, response.getOutputStream());
				response.flushBuffer();
			} finally {
				if (inputStream != null) {
					inputStream.close();
				}
			}
		} catch (Exception e) {
			response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			log.error(FILE_DOWNLOAD_FAILED, e);
			e.printStackTrace();
		}
	}

	public InputStream minioDownloader(String bucket, String objectName) {
		try {
			MinioClient minioClient = new MinioClient(url, userName, pwd);
			byte[] buff = new byte[1024];
			return MyMinioClient.downloader(minioClient, bucket, objectName);
		} catch (Exception e) {
			log.error(FILE_DOWNLOAD_FAILED, e);
			e.printStackTrace();
		}
		return null;
	}

	public byte[] downloader(String bucket, String objectName) {
		byte[] result = null;
		try {
			InputStream inputStream = null;
			try {
				inputStream = minioDownloader(bucket, objectName);
				if (inputStream == null) {
					return new byte[0];
				}
				result = org.apache.commons.io.IOUtils.toByteArray(inputStream);
				log.info("file:{} Already downloaded", objectName);
			} catch (IOException e) {
				log.error("minioDownloader or inputStream toByteArray is failed", e);
			} finally {
				if (inputStream != null) {
					inputStream.close();
				}
			}
		} catch (Exception e) {
			log.error(FILE_DOWNLOAD_FAILED, e);
		}
		return result;
	}

	/**
	 * 获取指定文件的预授权链接
	 *
	 * @param bucket
	 * @param objectName
	 * @param expiredSeconds 分享时长，若<=0则默认7天
	 * @return
	 */
	public String presignedGetObject(String bucket, String objectName, Integer expiredSeconds) throws MinioClientException {
		String signedUrl = null;

		try {
			MinioClient minioClient = new MinioClient(url, userName, pwd);
		} catch (Exception e) {
			log.error("Create Minio client failed", e);
			throw new MinioClientException(EnumWrapper.CodeAndMsg.E05020001,
					EnumWrapper.CodeAndMsg.E05020001.getMsg() + ": url=" + url + ", userName=" + userName + ", pwd=" + pwd);
		}
		try {
			minioClient.statObject(bucket, objectName);
		} catch (Exception e) {
			log.error("Minio file not found: bucket={}, objectName={}", bucket, objectName);
			e.printStackTrace();
			throw new MinioClientException(EnumWrapper.CodeAndMsg.E05060002);
		}
		try {
			if (expiredSeconds == null || expiredSeconds.intValue() <= 0) {
				signedUrl = minioClient.presignedGetObject(bucket, objectName);
			} else {
				signedUrl = minioClient.presignedGetObject(bucket, objectName, expiredSeconds);
			}
		} catch (Exception e) {
			log.error("Cannot get pre signed url: bucket={}, objectName={}", bucket, objectName);
			e.printStackTrace();
			throw new MinioClientException(EnumWrapper.CodeAndMsg.E05060003);
		}
		return signedUrl;
	}

	public MinioClient getMinioClient() throws InvalidPortException, InvalidEndpointException {
		return new MinioClient(url, userName, pwd);
	}

	/**
	 * 从MINIO批量下载
	 *
	 * @param fileList
	 * @param response
	 */
	public void downloader(String fileList, HttpServletResponse response, String tenantId, String userId) {
		String taskId = null;
		int len = 0;
		try {
			//store inputstream
			Map<String, InputStream> fileMap = new HashMap<>();
			ObjectMapper objectMapper = new ObjectMapper();
			List<BatchDownloadBean> files = objectMapper.readValue(fileList,
					new TypeReference<List<BatchDownloadBean>>() {
					});
			for (BatchDownloadBean file : files) {
				String bucket = file.getBucket();
				String objectName = file.getFileName();
				InputStream inputStream = minioDownloader(bucket, objectName);
				fileMap.put(objectName, inputStream);
			}
			//开始将下载下来的文件流,写入到压缩包中,并使用浏览器方式下载:
			String s = StringUtils.substringBeforeLast(files.get(0).getFileName(), ".");
			String downloadZipFileName = StringUtils.substringAfterLast(s, "__") + "等多文件.zip";
			//String downloadZipFileName = files.get(0).getFileName() + ".zip";
			try (
					ZipOutputStream out = new ZipOutputStream(response.getOutputStream())) {
				response.reset(); // 重点突出
				response.setCharacterEncoding(UTF8); // 重点突出
				response.setContentType("application/x-msdownload");// 不同类型的文件对应不同的MIME类型 // 重点突出

				//设置下载方式:
				//inline : 在浏览器中直接显示，不提示用户下载; -- 默认为inline方式
				//attachment : 弹出对话框，提示用户进行下载保存本地
				response.setHeader(CONTENT_DISPOSITION,
						"attachment;filename=" + URLEncoder.encode(downloadZipFileName, UTF8) + ";filename*=UTF-8''" + URLEncoder.encode(downloadZipFileName, UTF8));
				List<Long> list = new ArrayList<>();
				for (Map.Entry<String, InputStream> entry : fileMap.entrySet()) {
					//文件名:
					String fileName = entry.getKey();
					String newFileName = StringUtils.substringAfterLast(fileName, "__");
					//文件流:
					InputStream in = entry.getValue();
					Long size = null;
					if (in != null) {
						String mimeType = context.getMimeType(fileName);
						if (mimeType == null) {
							// set to binary type if MIME mapping not found
							mimeType = "application/octet-stream";
							log.info("context getMimeType is null");
						}
						out.putNextEntry(new ZipEntry(newFileName));
						size = Long.valueOf(IOUtils.copy(in, out));
						list.add(size);
						out.closeEntry();
						in.close();
					} else {
						response.setStatus(HttpStatus.SC_NOT_FOUND);
					}
					createDownloadInfo(fileName, tenantId, userId, size);
				}
				out.close();
				Long sum = list.stream().mapToLong(Long::longValue).sum();
				tenantContext.setTenantId(Long.valueOf(tenantId));
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("fileName", downloadZipFileName);
				map.put("transferType", "download");
				map.put("fileSize", sum + "");
				String json = objectMapper.writeValueAsString(map);
				taskId = sendStartTaskMq(json) + "";
				if (StringUtils.isNotBlank(taskId)) {
					sendTaskStatusMq(taskId, JobStatusEnum.ENDED.getCode());
				}
			}
			// Copy the stream to the response's output stream.
          /*  IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();*/
		} catch (Exception e) {
			sendTaskStatusMq(taskId, JobStatusEnum.FAIL.getCode());
			log.error(FILE_DOWNLOAD_FAILED, e);
			e.printStackTrace();
		}
	}

	public void downloader(String bucket, String objectName, HttpServletResponse response, String tenantId,
						   String userId) {
		String taskId = null;
		try {
			InputStream inputStream = minioDownloader(bucket, objectName);
			if (inputStream != null) {
				// String mimeType = context.getMimeType(objectName);
				// String mimeType = Files.probeContentType(Paths.get(objectName));
				String mimeType = mimeTypeConfig.getMimeType(objectName.substring(objectName.lastIndexOf('.') + 1));
				if (mimeType == null) {
					// set to binary type if MIME mapping not found
					mimeType = "application/octet-stream";
					System.out.println("context getMimeType is null");
				}
				// set content attributes for the response
				response.setContentType(mimeType);
				String newFileName;
				if (objectName.contains("__")) {
					newFileName = StringUtils.substringAfterLast(objectName, "__");
				} else {
					newFileName = objectName;
				}
				response.setHeader(CONTENT_DISPOSITION,
						"attachment;filename=" + URLEncoder.encode(newFileName, UTF8) + ";filename*=UTF-8''" + URLEncoder.encode(newFileName, UTF8));
				// Copy the stream to the response's output stream.
				try {
					Long in = Long.valueOf(IOUtils.copy(inputStream, response.getOutputStream()));
					createDownloadInfo(objectName, tenantId, userId, in);
					response.flushBuffer();
					tenantContext.setTenantId(Long.valueOf(tenantId));
					HashMap<String, String> map = new HashMap<>();
					map.put("fileName", newFileName);
					map.put("transferType", "download");
					map.put("fileSize", in + "");
					String json = objectMapper.writeValueAsString(map);
					taskId = sendStartTaskMq(json) + "";
					if (StringUtils.isNotBlank(taskId)) {
						sendTaskStatusMq(taskId, JobStatusEnum.ENDED.getCode());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				response.setStatus(HttpStatus.SC_NOT_FOUND);
			}
		} catch (Exception e) {
			sendTaskStatusMq(taskId, JobStatusEnum.FAIL.getCode());
			log.error(FILE_DOWNLOAD_FAILED, e);
			e.printStackTrace();
		}
	}

	private void createDownloadInfo(String objectName, String tenantId, String userId, Long in) throws DataServiceUserException {
		Fact instanceFact = null;
		CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
		CimDataSpace cds = null;
		try {
			cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
			modelCore.setCimDataSpace(cds);
			InfoObjectDef infoObjectDef =
					modelCore.getInfoObjectDef(CimConstants.BaseFileDownloadInfoKeys.baseFileDownloadObjectName);

			//文件下载记录到数据库
			Map<String, Object> generalInfo = new HashMap<>();
			generalInfo.put(CimConstants.BaseFileDownloadInfoKeys.FILE_NAME, objectName);
			generalInfo.put(CimConstants.BaseFileDownloadInfoKeys.FILE_SIZE, in);
			generalInfo.put(CimConstants.BaseFileDownloadInfoKeys.CREATOR, userId);
			generalInfo.put(CimConstants.BaseFileDownloadInfoKeys.CREATE_TIME, System.currentTimeMillis());
			InfoObjectValue objectValue = new InfoObjectValue();
			objectValue.setGeneralDatasetsPropertiesValue(generalInfo);
			InfoObject infoObject = infoObjectDef.newObject(objectValue, false);
		} finally {
			if (cds != null) {
				cds.closeSpace();
			}
		}
	}

	/**
	 * unzip zip file and upload to Minio
	 *
	 * @param bucketName
	 * @param zipFile
	 * @throws IOException
	 */
	public void unzipFileAndUpload(String tenantId, String bucketName, Path zipFile) throws IOException {
		FileSystem fs = FileSystems.newFileSystem(zipFile, null);
		Files.walkFileTree(fs.getPath("/"), new SimpleFileVisitor<Path>() {
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
				String storePath = tenantId + file.toString();
				log.debug("file store path: {}", storePath);
				try {
					minioClient.putObject(bucketName, storePath, fs.provider().newInputStream(file),
							URLConnection.guessContentTypeFromName(file.toString()));
				} catch (Exception e) {
					log.error("entity of zip file upload failed", e);
				}
				return FileVisitResult.CONTINUE;
			}
		});

	}

	public Long sendStartTaskMq(String json) {
		JobPropsDTO jobPropsDTO = new JobPropsDTO();
		// 文件传输 下载
		log.info("typeCode={}", fileDownload);
		jobPropsDTO.setJobName(fileDownloadPrefix + "-" + DateUtil.getCurrentDateReadable());
		jobPropsDTO.setTypeCode(fileDownload);
		// 消息内容
		jobPropsDTO.setParam(json);
		JobResponse<IdResult> jobResponse = jobInfoClient.add(jobPropsDTO);
		log.info("download response status code: {}, content: {}", jobResponse.getCode(), jobResponse.getData());
		return jobResponse.getData().getId();
	}

	/**
	 * 发送更改任务消息到MQ--更改文件任务状态
	 *
	 * @return
	 */
	public void sendTaskStatusMq(String taskId, String status) {
		JobParmDTO jobParmDTO = new JobParmDTO();
		jobParmDTO.setStatus(status);
		jobParmDTO.setDate(DateUtil.getCurrentDate());
		log.info("更改文件任务状态开始{}", DateUtil.getCurrentDate());
		jobInfoClient.updateStatus(Long.valueOf(taskId), jobParmDTO);
	}

	public List<FileStructBean> zipFileStructure(String bucket, String fileName,
												 FileImportTypeEnum fileImportTypeEnum) {
		List<FileStructBean> zipFileStructureBeans = new ArrayList<>();
		Assert.hasText(bucket, "bucket name is mandatory");
		Assert.hasText(fileName, "fileName name is mandatory");
		try {
			switch (fileImportTypeEnum) {
				case MAX:
					Iterable<Result<Item>> minioObjectList = minioClient.listObjects(bucket, fileName.substring(0,
							fileName.trim().lastIndexOf('.')));
					zipFileStructureBeans = maxZipStructure(bucket, minioObjectList);
					break;
				default:
					zipFileStructureBeans = dataImportService.getStruct(bucket, fileName, fileImportTypeEnum);
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		return zipFileStructureBeans;
	}

	public List<FileStructBean> maxZipStructure(String bucket, Iterable<Result<Item>> minioObjectList) {
		// Set<String> xmlFileName = new HashSet<>();

		Map<String, String> xmlFileName = new HashMap<>();
		Map<String, String> maxFileName = new HashMap<>();
		for (Result<Item> rt : minioObjectList) {
			Item item;
			try {
				item = rt.get();
				if (item.isDir()) {
					continue;
				}
				String tmpName = item.objectName();
				String tmpNameUpperCase = tmpName.toUpperCase();
				log.debug("object name: [{}]", tmpName);
				if (tmpNameUpperCase.endsWith("XLSX") || tmpNameUpperCase.endsWith("XLS")) {
					int lastIdx = tmpName.lastIndexOf('/') + 1;
					String srcFileName;
					if (lastIdx >= tmpName.length()) {
						srcFileName = tmpName;
					} else {
						srcFileName = tmpName.substring(lastIdx);
					}
					xmlFileName.put(srcFileName.substring(0, srcFileName.lastIndexOf('.')), tmpName);
				} else if (tmpNameUpperCase.endsWith("MAX")) {
					int lastIdx = tmpName.lastIndexOf('/') + 1;
					String srcFileName;
					if (lastIdx >= tmpName.length()) {
						srcFileName = tmpName;
					} else {
						srcFileName = tmpName.substring(lastIdx);
					}
					maxFileName.put(srcFileName.substring(0, srcFileName.lastIndexOf('.')), tmpName);
				}
			} catch (Exception e) {
				log.error("get object failed", e);
			}
		}
		List<FileStructBean> zipFileStructureBeanList = new ArrayList<>();
		log.debug("xml files: [{}]", xmlFileName);
		log.debug("max files: [{}]", maxFileName);
		if (maxFileName.size() > 0) {
			for (Map.Entry<String, String> entry : maxFileName.entrySet()) {
				FileStructBean tmpZipStructure = new FileStructBean(entry.getValue());
				if (xmlFileName.containsKey(entry.getKey())) {
					Map<String, String> fileStructure = readExcelHeader(bucket, xmlFileName.get(entry.getKey()));
					//TODO file structure
					tmpZipStructure.setStruct(fileStructure);
				}
				zipFileStructureBeanList.add(tmpZipStructure);
			}
		}
		return zipFileStructureBeanList;
	}

	public Map<String, String> readExcelHeader(String bucket, String fileName) {
		Map<String, String> fileStructure = new HashMap<>();
		try (InputStream inputStream = minioClient.getObject(bucket, fileName)) {
			List<Map<Integer, String>> excelValues = ExcelFileReader.oneSheetContent(inputStream, 0, 0, 1);
			if (excelValues.size() > 0) {
				Map<Integer, String> firstRow = excelValues.get(0);
				for (Map.Entry<Integer, String> entry : firstRow.entrySet()) {
					fileStructure.put(entry.getValue(), FileDataImportService.GisPropertyTypeMapping.get("string"));
				}
			} else {
				log.warn("excel records should greater than 2: [{}]", fileName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileStructure;
	}

	public List<Map<String, String>> readExcelContent(String bucket, String fileName) {
		List<Map<String, String>> fileStructure = new ArrayList<>();
		try (InputStream inputStream = minioClient.getObject(bucket, fileName)) {
			List<Map<Integer, String>> excelValues = ExcelFileReader.oneSheetContent(inputStream, 0, -1, -1);
			if (excelValues.size() > 1) {
				fileStructure = firstRowAsHeader(excelValues);
			} else {
				log.warn("excel records should greater than 2: [{}]", fileName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileStructure;
	}

	public List<Map<String, String>> firstRowAsHeader(List<Map<Integer, String>> content) {
		Assert.notEmpty(content, "no content in this file");
		List<Map<String, String>> mapList = new ArrayList<>();
		Map<Integer, String> headRow = content.get(0);

		for (int idx = 1; idx < content.size(); idx++) {
			Map<Integer, String> tmpRow = content.get(idx);
			Map<String, String> oneRowValue = new HashMap<>();
			for (Map.Entry<Integer, String> entry : tmpRow.entrySet()) {
				String type = headRow.get(entry.getKey());
				if (type != null) {
					oneRowValue.put(type, entry.getValue());
				}
			}
			mapList.add(oneRowValue);
		}
		return mapList;
	}

	public DeleteFileOutputBean deleteIfExists(DeleteFileInputBean deleteInput) {
		DeleteFileOutputBean deleteFileOutput = new DeleteFileOutputBean();
		deleteFileOutput.setBucket(deleteInput.getBucket());
		Map<String, Boolean> deleteResult = new HashMap<>();
		deleteFileOutput.setDeleteResults(deleteResult);

		for (String fn : deleteInput.getFileNames()) {
			deleteResult.put(fn, true);
		}

		for (Result<DeleteError> errorResult : minioClient.removeObject(deleteInput.getBucket(),
				deleteInput.getFileNames())) {
			DeleteError error = null;
			try {
				error = errorResult.get();
			} catch (Exception e) {
				e.printStackTrace();
			}
			log.info("Failed to remove '" + error.objectName() + "'. Error:" + error.message());
			deleteResult.put(error.objectName(), false);
		}
		return deleteFileOutput;
	}

	public void setMinioClient(MinioClient minioClient) {
		this.minioClient = minioClient;
	}


	/**
	 * url: http://10.129.57.108:9000/
	 * shareUrl: http://pcop.glodon.com/pcoposs/
	 * user-name: pcop
	 * #    password: pcoppcop
	 * password: gldcim.123
	 * bucket: pcop-cim
	 *
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			MinioClient minioClient = new MinioClient("", "pcop", "pcoppcop");
		} catch (InvalidEndpointException e) {
			e.printStackTrace();
		} catch (InvalidPortException e) {
			e.printStackTrace();
		}

//		mi.imageUpload2Cim("全国省界、市界的行政边界数据_WGS1984.zip", "C:\\Users\\yuanjk\\Downloads\\全国省界、市界的行政边界数据_WGS1984.zip");
		System.out.println("开始上传");
		String contentType = MediaType.APPLICATION_PDF_VALUE;
		// String objectName = "GIT-theory-tttt.pdf";
		String objectName = "GIT-theory-tttt";
		// mi.fileUpload("属性类型.json02", "C:\\Users\\yuanjk\\Downloads\\属性类型.json", contentType);
		// minioClient.putObject("temp", objectName, "G:\\tmp\\" + objectName, contentType);
		// minioClient.putObject("temp", objectName, "G:\\tmp\\" + objectName);
//		minioClient.putObject("pcop-cim", "Minio上传测试", "C:\\Users\\yuanjk\\Downloads\\Minio上传测试");

		// String shareUrl = minioClient.presignedGetObject("temp", objectName);

		// System.out.println("shareUrl: " + shareUrl);
		String mt = URLConnection.guessContentTypeFromName(objectName);
		System.out.println(objectName + "11: " + mt);
		System.out.println(objectName + "22: " + MimeTypeUtils.parseMimeType(mt));
		System.out.println(objectName + "33: " + contentType);


		System.out.println("上传结束");
	}


}
