package com.pactera.test;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by pactera on 2017/11/22.
 */
@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试
@ContextConfiguration({"/applicationContext.xml"})
public class MonthReporTest {

   @Autowired
   private RepositoryService repositoryService;
   @Autowired
   private RuntimeService runtimeService;
   @Autowired
   private HistoryService historyService;
   @Autowired
   private TaskService taskService;
   @Test
   public void test1(){
      //清空流程
      List<Deployment> list = repositoryService.createDeploymentQuery().list();
      for (Deployment definition : list){
         repositoryService.deleteDeployment(definition.getId());
      }
      // 部署流程定义
      Deployment deployment = repositoryService.createDeployment().name("月度报表").addClasspathResource("diagrams/apply.bpmn20.xml").deploy();
      System.out.println("流程ID: "+deployment.getId());
      // 启动流程实例
      ProcessInstance instance = runtimeService.startProcessInstanceByKey("report");
      System.out.println("流程实例ID: "+instance.getId());
      //查询部署的任务
      Task task0 = taskService.createTaskQuery().singleResult();
      System.out.println("任务ID: "+task0.getId());
      // 获得第一个任务
      List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("sales").list();
      for (Task task : tasks) {
         System.out.println("Following task is available for sales group: " + task.getName());
         // 认领任务这里由foozie认领，因为fozzie是sales组的成员
         taskService.claim(task.getId(), "fozzie");
      }
      // 查看fozzie现在是否能够获取到该任务
      tasks = taskService.createTaskQuery().taskAssignee("fozzie").list();
      for (Task task : tasks) {
         System.out.println("Task for fozzie: " + task.getName());
         // 执行(完成)任务
         taskService.complete(task.getId());
      }
      // 现在fozzie的可执行任务数就为0了
      System.out.println("Number of tasks for fozzie: "
            + taskService.createTaskQuery().taskAssignee("fozzie").count());
      // 获得第二个任务
      tasks = taskService.createTaskQuery().taskCandidateGroup("management").list();
      for (Task task : tasks) {
         System.out.println("Following task is available for accountancy group:" + task.getName());
         // 认领任务这里由kermit认领，因为kermit是management组的成员
         taskService.claim(task.getId(), "kermit");
      }
      // 完成第二个任务结束流程
      for (Task task : tasks) {
         taskService.complete(task.getId());
      }
      // 核实流程是否结束,输出流程结束时间
      HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
            .processInstanceId(instance.getId()).singleResult();
      System.out.println("Process instance end time: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(historicProcessInstance.getEndTime()));
   }
}
