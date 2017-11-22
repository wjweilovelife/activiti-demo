package com.pactera.test;

import com.pactera.utils.FileUtils;
import org.activiti.bpmn.BpmnAutoLayout;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by pactera on 2017/11/22.
 */
@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试
@ContextConfiguration({"/applicationContext.xml"})
public class TestDynamicDeploy {

   @Autowired
   private RepositoryService repositoryService;
   @Autowired
   private RuntimeService runtimeService;
   @Autowired
   private HistoryService historyService;
   @Autowired
   private TaskService taskService;

   @Test
   public void testDynamicDeploy() throws IOException {
      BpmnModel model = new BpmnModel();
      Process process = new Process();
      model.addProcess(process);
      process.setId("my-process");

      process.addFlowElement(createStartEvent());
      process.addFlowElement(createUserTask("task1", "First task", "fred"));
      process.addFlowElement(createUserTask("task2", "Second task", "john"));
      process.addFlowElement(createEndEvent());

      process.addFlowElement(createSequenceFlow("start", "task1"));
      process.addFlowElement(createSequenceFlow("task1", "task2"));
      process.addFlowElement(createSequenceFlow("task2", "end"));
      // 2. Generate graphical information
      new BpmnAutoLayout(model).execute();

      // 3. Deploy the process to the engine
      Deployment deployment = repositoryService.createDeployment()
            .addBpmnModel("dynamic-model.bpmn", model).name("Dynamic process deployment")
            .deploy();

      // 4. Start a process instance
      ProcessInstance processInstance = runtimeService
            .startProcessInstanceByKey("my-process");

      // 5. Check if task is available
      List<org.activiti.engine.task.Task> tasks = taskService.createTaskQuery()
            .processInstanceId(processInstance.getId()).list();

      Assert.assertEquals(1, tasks.size());
      Assert.assertEquals("First task", tasks.get(0).getName());
      Assert.assertEquals("fred", tasks.get(0).getAssignee());

      // 6. Save process diagram to a file
      InputStream processDiagram = repositoryService
            .getProcessDiagram(processInstance.getProcessDefinitionId());
      FileUtils.copyInputStreamToFile(processDiagram, new File("target/diagram.png"));

      // 7. Save resulting BPMN xml to a file
      InputStream processBpmn = repositoryService
            .getResourceAsStream(deployment.getId(), "dynamic-model.bpmn");
      FileUtils.copyInputStreamToFile(processBpmn,
            new File("target/process.bpmn20.xml"));
   }

   private FlowElement createSequenceFlow(String start, String end) {
      SequenceFlow flow = new SequenceFlow();
      flow.setSourceRef(start);
      flow.setTargetRef(end);
      return flow;
   }

   private FlowElement createEndEvent() {
      EndEvent endEvent = new EndEvent();
      endEvent.setId("end");
      return endEvent;
   }

   private FlowElement createUserTask(String id, String name, String assignee) {
      UserTask userTask = new UserTask();
      userTask.setName(name);
      userTask.setId(id);
      userTask.setAssignee(assignee);
      return userTask;
   }

   private FlowElement createStartEvent() {
      StartEvent startEvent = new StartEvent();
      startEvent.setId("start");
      return startEvent;
   }
}
