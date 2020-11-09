``````
服务端口号：7121
1.首先需要增加要采集天气的城市
api参考
{host:7121}/swagger-ui.html#/

服务器会每隔一小时取一次天气数据，存入cim库中。

``````
``````
1.相关参考资料
参考标准： 

短期天气预报(风力计算)：http://c.gb688.cn/bzgk/gb/showGb?type=online&hcno=4741FC6129DC79A7428953C93DF0E7E2 
环境空气质量指数(AQI)技术规定：http://bz.mep.gov.cn/bzwb/dqhjbh/jcgfffbz/201203/W020120410332725219541.pdf


天气种类：https://github.com/jokermonn/-Api/blob/master/xiaomi_weather_status.json 

国内几家天气种类都是这样。
例如：千云数据
https://market.aliyun.com/products/57096001/cmapi029741.html?spm=5176.730005.productlist.d_cmapi029741.e0803524FbY27P&innerSource=search_%E5%A2%A8%E8%BF%B9%E5%A4%A9%E6%B0%94#sku=yuncode2374100001



城市编码见附件。（国内的天气城市编码都来自中国天气网，见附件）

参考的国内几家天气数据
小米天气
https://github.com/jokermonn/-Api/blob/master/XiaomiWeather.md
墨迹天气
https://market.aliyun.com/products/57096001/cmapi023656.html?spm=5176.730005.productlist.d_cmapi023656.e0803524dtW7mr&innerSource=search_%E5%A2%A8%E8%BF%B9%E5%A4%A9%E6%B0%94#sku=yuncode1765600000
中国天气网
https://www.cnblogs.com/yeminglong/p/3525158.html
``````

2.梳理的天气模型

``````
标准天气模型		
weatherInfo	type	标准天气模型
cityName	String	城市名字
cityId	String	城市id
condition	String	天气
conditionId	String	实时天气id
humidity	String	湿度
pressure	String	气压
realFeel	String	体感温度
temp	String	温度
uvi	String	紫外线强度
visibility	String	能见度高低
windDirection	String	风向
windForce	String	风力
windSpeed	String	风速
sunRise	Date	日出时间
sunSet	Date	日落时间
aqi	String	空气质量指数
aqiDetail	String	空气质量详情
pubTime	Date	更新时间
fetchDay	String	采集日期(精确到天)
fetchHour	String	采集日期(精确到小时)
``````
其他说明，其中aqiDetail字段为
``````
public class Aqi {
private String cityName; //城市名称
private String cityId; //城市名称
private String co; //co一氧化碳指数
private String coC;//co一氧化碳浓度
private String no2;//no2二氧化氮指数
private String no2C;//no2二氧化氮指数
private String so2;  //二氧化硫
private String so2C; //二氧化硫浓度
private String o3; //臭氧指数
private String o3C;//臭氧浓度
private String pm10;//pm1.0指数
private String pm10C;//pm1.0浓度
private String pm25;//pm2.5指数
private String pm25C; //pm2.5浓度
private String value; //空气指数值
private Date pubtime; //发布时间
}
的序列化存储
``````

3.cim存储示例（某前接入的为小米天气）
``````
@rid	@version	@class	temp	visibility	pubTime	windForce	uvi	pressure	realFeel	cityName	conditionId	aqi	humidity	aqiDetail	windDirection	windSpeed
#1445:0
1	GLD_IH_FACT_weatherInfo
18		2018-10-22 17:47:00	1	0	1017.0	18	青岛	0	42	62	{"cityId":"101120201","cityName":"青岛","co":"0.51","coC":"","no2":"35","no2C":"","o3":"96","o3C":"","pm10":"37","pm10C":"","pm25":"13","pm25C":"","pubtime":{"date":22,"day":1,"hours":17,"minutes":0,"month":9,"seconds":0,"time":1540198800000,"timezoneOffset":-480,"year":118},"so2":"6","so2C":"","value":"42"}	180	13.0
#1446:0
1	GLD_IH_FACT_weatherInfo
23		2018-10-22 17:47:00	1	0	1015.0	23	福州	0	31	69	{"cityId":"101230101","cityName":"福州","co":"0.60","coC":"","no2":"20","no2C":"","o3":"72","o3C":"","pm10":"30","pm10C":"","pm25":"15","pm25C":"","pubtime":{"date":22,"day":1,"hours":17,"minutes":0,"month":9,"seconds":0,"time":1540198800000,"timezoneOffset":-480,"year":118},"so2":"6","so2C":"","value":"31"}	315	18.5
``````