# 2022_OSD_ProgrammingAssignment2
***
Subject: Operatign Systems Design  
Prof: Youn-Ho Lee    
Student No.: 20102117  
Name: Lee Jeonghyeon    
***

## Summary
<b>This Project is designing a pid Manager that is responsible for managing pids.</b> When a process is first created, it is assigned a unique pid by the pid manager. The pid is returned to the pid manager when the process completes execution.<br>
Using Java Interface, the basic method for obtaining and releasing a pid is identified. The difference between getPID() and getPIDWait() is that if no pids are available, getPID() returns -1, whereas getPIDWait() blocks the calling process until a pid becomes available. These two methods can be selected by the user to output the results. It will explain the program executed by inputting the number of threads, the lifetime of the program, and the lifetime of the thread to the user.

## Implementation
The pidmanager interface was implemented, and functions were implemented by overriding each. Also, I used the runnable interface for thread generation.

## Result
#### Case1: Number of threads less than pid range, using getPID()
#### Case2: Number of threads more than pid range, using getPID()
#### Case3: Number of threads less than pid range, using getWaitPID()
#### Case4: Number of threads less than pid range, using getWaitPID()

