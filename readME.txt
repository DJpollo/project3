going to add terminal input of how everything runs
PS C:\Users\jaime\project3> javac IndexManager.java               
PS C:\Users\jaime\project3> java IndexManager create test.idx        
PS C:\Users\jaime\project3> java IndexManager insert test.idx 5 10   
Inserted key 5 with value 10
PS C:\Users\jaime\project3> java IndexManager print test.idx
5 -> 0
PS C:\Users\jaime\project3> java IndexManager search test.idx 5      
Key: 5, Value: 0
PS C:\Users\jaime\project3> java IndexManager load test.idx input.csv
PS C:\Users\jaime\project3> java IndexManager print test.idx         
5 -> 0
PS C:\Users\jaime\project3> java IndexManager load test.idx input.csv
PS C:\Users\jaime\project3> java IndexManager print test.idx
5 -> 0
300 -> 0
10 -> 0
30 -> 300