# 介绍
这个是Apache HugeGraph loader的服务化改造的demo版本，主要验证引入netty进行服务化和引入任务管理模块

## 使用

### 启动服务

运行`LoaderServer`类，在cmd窗口中可以运行一下命令

### 调用接口

#### 创建任务

``` 
curl -X POST http://localhost:8080/submit -H "Content-Type: text/plain" -d $'Alice\nBob'
```

示例：
```json
{
"status":"success",
"taskId":"21ac830c-72aa-4335-9ee5-0d84ec55156f"
}
```

### 根据任务ID查询任务

将任务ID进行替换
```
curl http://localhost:8080/status/{taskId}
```

示例：
```json
{
"taskId":"21ac830c-72aa-4335-9ee5-0d84ec55156f",
"taskName":"",
"state":"SUCCESS",
"createTime":1748855912324,
"startTime":1748855912427,
"endTime":1748855912429,
"sourceConfig":"",
"targetConfig":"",
"loadOptions":"",
"totalRecords":1,
"processedRecords":0,
"successRecords":0,
"failedRecords":0,
"progressPercentage":0.0,
"lastProcessedTimestamp":"",
"checkpointData":""
}
```

### 查询所有任务

```
curl -X GET http://localhost:8080/tasks
```

示例:
```json
[
{
"taskId":"4b3409c8-a753-4f35-91a1-c66ad9f1d518",
"taskName":"",
"state":"SUCCESS",
"createTime":1748856105122,
"startTime":1748856105129,
"endTime":1748856105131,
"sourceConfig":"",
"targetConfig":"",
"loadOptions":"",
"totalRecords":4,
"processedRecords":0,
"successRecords":0,
"failedRecords":0,
"progressPercentage":0.0,
"lastProcessedTimestamp":"",
"checkpointData":""
},
{
"taskId":"f0986564-e5ef-4092-b4a5-ce3dbcf3460e",
"taskName":"",
"state":"SUCCESS",
"createTime":1748855954201,
"startTime":1748855954215,
"endTime":1748855954218,
"sourceConfig":"",
"targetConfig":"",
"loadOptions":"",
"totalRecords":2,
"processedRecords":0,
"successRecords":0,
"failedRecords":0,
"progressPercentage":0.0,
"lastProcessedTimestamp":"",
"checkpointData":""
},
{
"taskId":"21ac830c-72aa-4335-9ee5-0d84ec55156f",
"taskName":"",
"state":"CANCELLED",
"createTime":1748855912324,
"startTime":1748855912427,
"endTime":1748856261880,
"sourceConfig":"",
"targetConfig":"",
"loadOptions":"",
"totalRecords":1,
"processedRecords":0,
"successRecords":0,
"failedRecords":0,
"progressPercentage":0.0,
"lastProcessedTimestamp":"",
"checkpointData":""
},
{
"taskId":"4333cfee-cff7-4600-b6ca-77a4f5a8a4e6",
"taskName":"",
"state":"SUCCESS",
"createTime":1748854431883,
"startTime":1748854431998,
"endTime":1748854432001,
"sourceConfig":"",
"targetConfig":"",
"loadOptions":"",
"totalRecords":7,
"processedRecords":0,
"successRecords":0,
"failedRecords":0,
"progressPercentage":0.0,
"lastProcessedTimestamp":"",
"checkpointData":""
},
{
"taskId":"b9682703-d0ce-45ef-a7b4-81561b79a90a",
"taskName":"",
"state":"SUCCESS",
"createTime":1748854289940,
"startTime":1748854290050,
"endTime":1748854290053,
"sourceConfig":"",
"targetConfig":"",
"loadOptions":"",
"totalRecords":7,
"processedRecords":0,
"successRecords":0,
"failedRecords":0,
"progressPercentage":0.0,
"lastProcessedTimestamp":"",
"checkpointData":""
},
{
"taskId":"84808c2d-4919-4ca7-87f8-15fef0c7bae0",
"taskName":"",
"state":"SUCCESS",
"createTime":0,
"startTime":1748853502641,
"endTime":1748853502643,
"sourceConfig":"",
"targetConfig":"",
"loadOptions":"",
"totalRecords":7,
"processedRecords":1,
"successRecords":0,
"failedRecords":0,
"progressPercentage":14.285714285714285,
"lastProcessedTimestamp":"",
"checkpointData":""
},
{
"taskId":"f36dba84-40e9-4fa9-9d7f-c750e9c6593a",
"taskName":"",
"state":"SUCCESS",
"createTime":0,
"startTime":1748853767824,
"endTime":1748853767827,
"sourceConfig":"",
"targetConfig":"",
"loadOptions":"",
"totalRecords":7,
"processedRecords":1,
"successRecords":0,
"failedRecords":0,
"progressPercentage":14.285714285714285,
"lastProcessedTimestamp":"",
"checkpointData":""
}
]
```
### 取消任务

```
curl -X POST http://localhost:8080/cancel/21ac830c-72aa-4335-9ee5-0d84ec55156f
```

示例：
```text
任务已取消: 21ac830c-72aa-4335-9ee5-0d84ec55156f
```