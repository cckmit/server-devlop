package com.glodon.pcop.cimsvc.service;

import com.glodon.pcop.cimsvc.model.worker.WorkerPerProjectBean;
import com.glodon.pcop.cimsvc.model.worker.WorkerPerTeamBean;
import com.glodon.pcop.cimsvc.model.worker.WorkerPerTypeBean;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;


/**
 * @author yuanjk(yuanjk @ glodon.com)
 * @date 2018/8/5 16:21
 */
@Service
public class WorkerCountService {
    SecureRandom rd = new SecureRandom(); // Compliant for security-sensitive use cases

    /**
     * 每个项目实时在场人数和在场总数
     */
    public List<WorkerPerProjectBean> getWorkerCountPerProject() {
        List<WorkerPerProjectBean> list = new ArrayList<>();

        for (int i = 0; i < 15; i++) {
            int countOnhand = rd.nextInt(1000);
            int countIntime = rd.nextInt(1000);
            WorkerPerProjectBean wp = new WorkerPerProjectBean("p" + i, "项目" + i, (countOnhand + countIntime) + "", countIntime + "");
            list.add(wp);
        }
        return list;
    }

    /**
     * 获取每个队伍的人数
     *
     * @return
     */
    public List<WorkerPerTeamBean> getWorkerCountPerTeam() {
        List<WorkerPerTeamBean> list = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            int count = rd.nextInt(1000);
            WorkerPerTeamBean wt = new WorkerPerTeamBean("t" + i, "队伍" + i, count + "");
            list.add(wt);
        }
        return list;
    }

    /**
     * 获取每个工种的人数
     *
     * @return
     */
    public List<WorkerPerTypeBean> getWorkerCountPerWorkType() {
        List<WorkerPerTypeBean> list = new ArrayList<>();
        String[] workTypes = {"木工", "架子工", "钢筋工", "电工", "油漆工", "瓦工", "水电工"};
        for (int i = 0; i < workTypes.length; i++) {
            int count = rd.nextInt(1000);
            WorkerPerTypeBean wtb = new WorkerPerTypeBean("wt" + i, workTypes[i], count + "");
            list.add(wtb);
        }
        return list;
    }

}

