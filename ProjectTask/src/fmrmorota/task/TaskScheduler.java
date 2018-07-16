package fmrmorota.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TaskScheduler{

    public static void main(String[] args) {

        Map<Task, List<Task>> taskMap = new HashMap<>();
        List<Task> taskList = new ArrayList<>();

        String pattern = "MM/dd/yyyy EEE";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        Calendar cal = Calendar.getInstance();

        String projectStartDate  = "07/01/2018 Sun";

        Task firstTask = new Task();
        firstTask.setName("Task A");
        firstTask.setDuration(4);

        Task secondTask = new Task();
        secondTask.setName("Task B");
        secondTask.setDuration(1);

        Task thirdTask = new Task();
        thirdTask.setName("Task C");
        thirdTask.setDuration(5);

        Task fourthTask = new Task();
        fourthTask.setName("Task D");
        fourthTask.setDuration(6);

        Task fifthTask = new Task();
        fifthTask.setName("Task E");
        fifthTask.setDuration(7);

        Task sixthTask = new Task();
        sixthTask.setName("Task F");
        sixthTask.setDuration(1);

        Task seventhTask = new Task();
        seventhTask.setName("Task G");
        seventhTask.setDuration(2);

        //add task to task list
        taskList.add(firstTask);
        taskList.add(secondTask);
        taskList.add(thirdTask);
        taskList.add(fourthTask);
        taskList.add(fifthTask);
        taskList.add(sixthTask);
        taskList.add(seventhTask);

        List<Task> firstTaskDeps = new ArrayList<>();
        List<Task> secondTaskDeps = new ArrayList<>();
        List<Task> thirdTaskDeps = new ArrayList<>();
        List<Task> fourthTaskDeps = new ArrayList<>();
        List<Task> fifthTaskDeps = new ArrayList<>();
        List<Task> sixthTaskDeps = new ArrayList<>();
        List<Task> seventhTaskDeps = new ArrayList<>();

        //add task dependencies
//        Test 1
//        firstTaskDeps.add(thirdTask);
//        secondTaskDeps.add(sixthTask);
//        thirdTaskDeps.add(fourthTask);
//        thirdTaskDeps.add(fifthTask);
//        fifthTaskDeps.add(seventhTask);
//        sixthTaskDeps.add(fifthTask);
//        Test 2
        firstTaskDeps.add(thirdTask);
        secondTaskDeps.add(sixthTask);
        thirdTaskDeps.add(fourthTask);
        thirdTaskDeps.add(fifthTask);
        thirdTaskDeps.add(sixthTask);
        sixthTaskDeps.add(seventhTask);
        seventhTaskDeps.add(fifthTask);

        //map task to task dependencies
        taskMap.put(firstTask, firstTaskDeps);
        taskMap.put(secondTask, secondTaskDeps);
        taskMap.put(thirdTask, thirdTaskDeps);
        taskMap.put(fourthTask, fourthTaskDeps);
        taskMap.put(fifthTask, fifthTaskDeps);
        taskMap.put(sixthTask, sixthTaskDeps);
        taskMap.put(seventhTask, seventhTaskDeps);

        try {
            while (true) { //loop through until all tasks are finished
                for (Task task : taskList) {
                    String dateHolder;
                    if (!task.isIsEnded()) {

                        //Start and end tasks without dependents
                        task.setStart(projectStartDate);
                        cal.setTime(formatter.parse(task.getStart()));

                        adjust(cal);

                        dateHolder = formatter.format(cal.getTime());
                        task.setStart(dateHolder);

                        cal.add(Calendar.DATE, task.getDuration());

                        adjust(cal);

                        dateHolder = formatter.format(cal.getTime());

                        task.setEnd(dateHolder);
                        task.setIsEnded(true);
                    }
                    //go through each child of task
                    for (Task taskInner : taskMap.get(task)) {
                        
                        String innerTemp;
                        String entryTemp;
                        Calendar innerCal = Calendar.getInstance();
                        Calendar entryCal = Calendar.getInstance();

                        for (Entry<Task, List<Task>> entry : taskMap.entrySet()) {
                            if (entry.getKey().equals(task)) {
                                //check if parent task has already ended
                                if (entry.getKey().isIsEnded()) {
                                    //child task started and ended but has other parent that needs to be checked

                                    entryCal.setTime(formatter.parse(entry.getKey().getEnd()));

                                    if (!taskInner.getStart().isEmpty() && !taskInner.getEnd().isEmpty()) {

                                        innerCal.setTime(formatter.parse(taskInner.getStart()));

                                        //check latest date of parent task then set as child task start
                                        if (entryCal.after(innerCal)) {
                                            entryCal.add(Calendar.DATE, 1);

                                            adjust(entryCal);

                                            entryTemp = formatter.format(entryCal.getTime());
                                            taskInner.setStart(entryTemp);

                                            innerCal.setTime(formatter.parse(taskInner.getStart()));
                                            innerCal.add(Calendar.DATE, taskInner.getDuration());

                                            adjust(innerCal);

                                            innerTemp = formatter.format(innerCal.getTime());
                                            taskInner.setEnd(innerTemp);
                                            taskInner.setIsEnded(true);

                                        }
                                    } else { //child task not yet started

                                        entryCal.add(Calendar.DATE, 1);

                                        adjust(entryCal);

                                        entryTemp = formatter.format(entryCal.getTime());

                                        taskInner.setStart(entryTemp);

                                        innerCal.setTime(formatter.parse(taskInner.getStart()));
                                        innerCal.add(Calendar.DATE, taskInner.getDuration());

                                        adjust(innerCal);

                                        innerTemp = formatter.format(innerCal.getTime());
                                        taskInner.setEnd(innerTemp);
                                        taskInner.setIsEnded(true);
                                    }
                                }
                            }
                        }
                    }
                }

                //check if all tasks are finished
                boolean bool = false;
                for (Task ender : taskList) {
                    bool = ender.isIsEnded();
                }
                if (bool) {
                    break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        //Print
        System.out.println("===========================");
        System.out.println("");

        for (Task t : taskList) {
            System.out.println("Name: " + t.getName());
            System.out.println("Duration: " + t.getDuration());
            for (Task task : taskMap.get(t)) {
                System.out.println("Dependents: " + task.getName());
            }

            System.out.println("Start: " + t.getStart());
            System.out.println("End: " + t.getEnd());
            System.out.println("");
        }

        System.out.println("===========================");
    }

    public static void adjust(Calendar cal) {
        //Do not include weekends
        if (cal.get(Calendar.DAY_OF_WEEK) == 1) {
            cal.add(Calendar.DATE, 1);
        } else if (cal.get(Calendar.DAY_OF_WEEK) == 7) {
            cal.add(Calendar.DATE, 2);
        }
    }

}
