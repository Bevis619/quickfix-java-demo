# GSX FIX API

简介

```
GSX FIX API 是交易所面向外部开发者提供的开发接口。
```

------

更新历史

| 版本        | 时间       | 说明   | 作者     |
| --------- | -------- | ---- | ------ |
| v1.0.0(α) | 20190328 | 创建文档 | yanghuadong |

------

## 一、全局规则

#### 接口访问地址信息

- 接口访问地址：10.200.174.57
- 接口访问端口：30010
- 接口访问协议：TCP(SSL)

#### 接口规则

- 采用SSL socket长连接访问
- 接口请求均采用 FIX(4.4) 协议
- 接口统一请求数据满足FIX协议数据格式
- 接口统一返回数据满足FIX协议数据格式
- 当服务端接收到用户请求后，会在稍后进行相应，具体的响应格式参考下列参数列表
- 接口调用前均需要登录，登录方式及参数请参考下列参数列表

##### 全局数据格式定义

参数约定 

```
1. 参数说明：
   1.1请求参数：
       - 客户端固定参数：
       - BeginString=FIX.4.4
       - TargetCompID=GSX
       - HeartBtInt=30
   1.2响应参数：
       - 具体响应信息请参考下方接口列表，异常说明请参考下方的【全局通用状态码】表。
2. 签名:
   所有请求均需要按本文档中的签名规则传递参数签名，签名生成规则请参考下方的参数签名规范；
3. 其它用户资产等可通过REST接口获取；
4. 发送请求之前，需先发送Logon消息进行登录；
5. 建议使用fix协议框架quickfix
```

##### 登录签名描述 

```
交易所将对请求数据的内容进行验签，以确定携带的信息是否未经篡改，因此定义生成 sign 字符串的方法。

a. Logon请求中，将SendingTime,MsgType,MsgSeqNum,SenderCompID,TargetCompID,$secretKey将参数按参数名从小到大自然排序，并用","连接进行组合md5加密签名生成签名sign;

b. Logon请求中，SendingTime,MsgType,MsgSeqNum,SenderCompID,TargetCompID为必传字段；

c. 为了账户安全，Logon请求中，请勿传递$secretKey；

d. Logon请求中，将sign作为RawData设置到Logon的message中，sign中的字母请采用小写;

注：
SenderCompID=$accessKey；
$accessKey和$secretKey通过开通API获得
```

[开通API](https://premium-exp.coinsuper.com)

#####  签名生成方法示例 

```
假设有一个调用请求，其中，
$accesskey : 7d8f8655-ce10-428d-b10a-b9dcc25b352d，$secretkey : d741bc45-d53a-4343-b97b-0f5f179ce8fe，TargetCompID : GSX

Logon请求参数如下:
"MsgSeqNum" -> "1"
"MsgType" -> "A"
"SenderCompID" -> "7d8f8655-ce10-428d-b10a-b9dcc25b352d"
"SendingTime" -> "220190328-07:56:22.176"
"TargetCompID" -> "GSX"

i：经过 a 过程排序后的字符串 string 为：
d741bc45-d53a-4343-b97b-0f5f179ce8fe,1,A,7d8f8655-ce10-428d-b10a-b9dcc25b352d,20190328-07:56:22.176,GSX

ii：经过 b 过程后得到 sign 为 :
signValue = md5(string)=7560481ce09c40be9bd1995132bef068;

完整请求数据 :
8=FIX.4.49=13635=A34=149=7d8f8655-ce10-428d-b10a-b9dcc25b352d52=20190328-07:56:22.17656=GSX95=3296=7560481ce09c40be9bd1995132bef06898=0108=3010=243
```

#### FIX请求标准消息头

注：每个接口请求必须传递的消息参数，具体使用方式请参考下方接口定义及请求示例

| 字段名          | 字段Code | 填写类型 | 描述                            |
| ------------ | ------ | ---- | ----------------------------- |
| BeginString  | 8      | 必填   | FIX协议版本(固定值：FIX.4.4)          |
| BodyLength   | 9      | 必填   | 消息长度的字节数. 永远在整条消息的第二个字段(不加密). |
| MsgType      | 35     | 必填   | 请求消息类型                        |
| MsgSeqNum    | 34     | 必填   | 整型消息序列号.                      |
| SenderCompID | 49     | 必填   | $accesskey，通过开通API获得          |
| SendingTime  | 52     | 必填   | 请求发送时间(UTC时间)                 |
| TargetCompID | 56     | 必填   | 服务端标识符，请参考上方[登录签名描述]          |

####  全局通用异常信息 

| 异常text                       | 异常描述                           |
| ---------------------------- | ------------------------------ |
| user not exist              | 错误的accesskey                |
| no permissions               | 未开通权限                     |
| the parameter 'HeartBtInt' is fixed to 30 seconds| 心跳时间固定为30s |
| failed to verify signature   | 验证签名失败 |
| sys error                    | 系统内部异常                         |

注：异常信息通常以Reject响应返回；各接口有更详细的异常信息定义；

------



## 二、详细接口定义

### API 接口定义

#### 1. 会话类

##### 1.1 登录

功能描述: 

登录请求（会话长连接由登录成功时创建，其他所有请求都需依赖登录成功）

请求MsgType:

```
Logon(A)
```

接口请求参数:

| 字段名          | 字段Code | 类型 | 描述                          |
| ------------ | ------ | ---- | --------------------------- |
| EncryptMethod| 98     | body   | 0-明文      |
| HeartBtInt   | 108    | body   | 固定参数，请使用上方[参数约定]中描述的值       |
| RawData      | 96     | body   | 登录参数签名( 签名规则请参考上方[登录签名描述] ) |
| RawDataLength| 95     | body   | 登录参数签名长度|

响应MsgType：

```
Logon(A)
```

响应参数:

| 字段名          | 字段Code | 类型 | 描述                          |
| ------------ | ------ | ---- | --------------------------- |
| EncryptMethod| 98     | body   | 0-明文   |
| HeartBtInt   | 108    | body   | 30-固定值       |

请求示例：  

```
8=FIX.4.49=13635=A34=149=7d8f8655-ce10-428d-b10a-b9dcc25b352d52=20190328-07:56:22.17656=GSX95=3296=7560481ce09c40be9bd1995132bef06898=0108=3010=243
```

登陆成功返回示例: 

```
8=FIX.4.49=9435=A34=149=GSX52=20190328-07:56:22.38956=7d8f8655-ce10-428d-b10a-b9dcc25b352d98=0108=3010=168
```

登陆失败返回示例：MsgType为Logout(5)
```
8=FIX.4.49=11235=534=249=GSX52=20190403-02:43:33.15056=7d8f8655-ce10-428d-b10a-b9dcc25b352d58=failed to verify signature10=074
```

------

##### 1.2 注销

功能描述: 

注销请求，发起后将会关闭会话长连接

请求MsgType:

```
Logout(5)
```

接口请求参数:

无

响应MsgType：

```
Logout(5)
```

响应参数:

无

请求示例：  

```
8=FIX.4.49=8335=534=4349=7d8f8655-ce10-428d-b10a-b9dcc25b352d52=20190328-08:17:05.14156=GSX10=168
```

  返回示例: 

```
8=FIX.4.49=8335=534=4349=GSX52=20190328-08:17:05.14656=7d8f8655-ce10-428d-b10a-b9dcc25b352d10=173
```

------

##### 1.3 心跳

功能描述: 

心跳请求，固定时间发送，用于维持会话长连接，服务端30秒内仅会响应一次

请求MsgType:

```
Heartbeat(0)
```

接口请求参数:

无

响应MsgType:

```
Heartbeat(0)
```

响应参数:

无

请求示例:

```
8=FIX.4.49=8235=034=449=7d8f8655-ce10-428d-b10a-b9dcc25b352d52=20190328-07:57:53.12956=GSX10=123
```

 返回示例: 

```
8=FIX.4.49=8235=034=349=GSX52=20190328-07:57:23.13256=7d8f8655-ce10-428d-b10a-b9dcc25b352d10=113
```

------

#### 2. 交易类

##### 2.1 下单委托

功能描述: 

交易下单委托请求

请求MsgType:

```
NewOrderSingle(D)
```

接口请求参数:

| 字段名       | 字段Code | 填写类型 | 描述                                                    |
| ------------ | -------- | -------- | ------------------------------------------------------- |
| ClOrdID      | 11       | 必填     | 客户端自定义的订单ID                       |
| Symbol       | 55       | 必填     | 交易对                                                  |
| Price        | 44       | 必填     | 成交限价(限价单专用，市价单请传入0)                     |
| Side         | 54       | 必填     | 买卖类型( BUY (1) = 买单，SELL (2) = 卖单 )             |
| OrdType      | 40       | 必填     | 委托单类型( MARKET (1) = 市价，LIMIT (2) = 限价 )       |
| OrderQty     | 38       | 必填     | 标的币数量(限价买，限价卖，市价卖时使用。市价买请填0)   |
| CashOrderQty | 152      | 必填     | 计价币数量(市价买时使用。限价买，限价卖，市价卖时请填0) |
| TransactTime | 60       | 必填     | 请求时间(UTC时间)                                       |

```
参数附加说明：

BTC/USD交易对，OrderQty和Amount使用举例：

- 6300USD限价买0.1 BTC：Symbol=BTC/USD,Side=1,OrdType=2,Price=6300,OrderQty=0.1,CashOrderQty=0;

- 6300USD限价卖0.2 BTC：Symbol=BTC/USD,Side=2,OrdType=2,Price=6300,OrderQty=0.2,CashOrderQty=0;

- 500USD市价买BTC：Symbol=BTC/USD,Side=1,OrdType=1,Price=0,OrderQty=0,CashOrderQty=500;

- 市价卖0.5BTC：Symbol=BTC/USD,Side=2,OrdType=1,Price=0,OrderQty=0.5,CashOrderQty=0;
```

成功响应MsgType：

```
ExecutionReport(8)
```

成功响应参数:

| 字段名       | 字段Code | 描述                                 |
| ------------ | -------- | ------------------------------------ |
| ClOrdID      | 11       | 客户端请求唯一标识符                 |
| OrderID      | 37       | 服务端生成的订单ID                   |
| OrigClOrdID  | 41       | 客户端订单ID（为 *）                 |
| Side         | 54       | 有效值：1-Buy(买入) 、2-Sell（卖出） |
| Symbol       | 55       | 交易对                               |
| TransactTime | 60       | 响应消息发送时间(UTC时间)            |
| ExecType     | 150      | 执行类型，固定值=0（NEW）            |
| ExecID       | 17       | 执行ID，为服务端订单ID               |
| OrdStatus    | 39       | 订单状态，固定值=0（NEW）            |
| LeavesQty    | 151      | 未成交数量，固定值=0.0                 |
| CumQty       | 14       | 已成交数量，固定值=0.0                 |
| AvgPx        | 6        | 成交均价，固定值=0.0                   |

失败响应MsgType：

```
ExecutionReport(8)
```

失败响应参数:

| 字段名       | 字段Code | 描述                                 |
| ------------ | -------- | ------------------------------------ |
| ClOrdID      | 11       | 客户端请求唯一标识符                 |
| OrderID      | 37       | 服务端生成的订单ID（为null）         |
| OrigClOrdID  | 41       | 客户端订单ID（为 *）                 |
| Side         | 54       | 有效值：1-Buy (买入) 、2-Sell (卖出) |
| Symbol       | 55       | 交易对                               |
| TransactTime | 60       | 响应消息发送时间(UTC时间)            |
| ExecType     | 150      | 执行类型，固定值=0（NEW）            |
| ExecID       | 17       | 执行ID，为服务端订单ID（为null）     |
| OrdStatus    | 39       | 订单状态，固定值=0（NEW）            |
| LeavesQty    | 151      | 未成交数量，固定值=0.0                 |
| CumQty       | 14       | 已成交数量，固定值=0.0                 |
| AvgPx        | 6        | 成交均价，固定值=0.0                   |
| Text         | 58       | 备注                                 |

请求示例：  

```
8=FIX.4.49=18135=D34=949=7d8f8655-ce10-428d-b10a-b9dcc25b352d50=162088347629577420152=20190404-03:38:52.26256=GSX11=IT00138=0.140=244=630054=155=BTC/USD60=20190404-03:38:52.262152=010=213
```

失败返回示例: 

```
8=FIX.4.49=21635=834=249=GSX52=20190404-03:46:10.09856=7d8f8655-ce10-428d-b10a-b9dcc25b352d6=011=IT00114=017=null37=null39=041=*54=155=BTC/USD58=order symbol has not been existed60=20190404-03:46:10.094150=0151=010=188
```
成功返回示例:

```
8=FIX.4.49=21635=834=249=GSX52=20190404-03:46:10.09856=7d8f8655-ce10-428d-b10a-b9dcc25b352d6=011=IT00114=017=162088347629577420137=162088347629577420139=041=*54=155=BTC/USD60=20190404-03:46:10.094150=0151=010=188
```



| 异常text                                       | 异常描述             |
| ---------------------------------------------- | -------------------- |
| symbol not trading                             | 交易对不能交易       |
| order symbol has not been existed              | 交易对不存在         |
| order amount or quantity less than min setting | 交易数量小于要求的值 |
| price out of range                             | 价格超出范围         |
| action is invalid                              | 不支持的交易类型     |
| order type is invalid                          | 不支持的订单类型     |
| user account forbidden                         | 账户禁止交易         |
| account balance is not enough                  | 余额不足             |
| price is invalid                               | price无效            |
| quantity is invalid                            | cashOrderQty无效     |
| amount is invalid                              | OrderQty无效         |
| create order failure                           | 订单创建失败         |

------

##### 2.2 撤单委托

功能描述: 

交易撤单委托请求

请求MsgType:

```
OrderCancelRequest(F)
```

接口请求参数:

| 字段名     | 字段Code | 是否必填 | 描述         |
| ------- | ------ | ---- | ---------- |
| ClOrdID | 11     | 必填   | 客户端请求唯一标识符 |
| OrderID | 37     | 必填   | 服务端生成的订单ID |
| OrigClOrdID | 41     | 必填   | 客户端订单ID |
| Side | 54     | 必填   | 有效值：1-Buy(买入) 、2-Sell（卖出） |
| Symbol | 55     | 必填   | 交易对 |
| TransactTime | 60     | 必填   | 请求消息发送时间 （UTC时间） |

撤单成功响应MsgType：

```
ExecutionReport(8)
```

响应参数:

| 字段名          | 字段Code | 描述                         |
| ------------ | ------ | -------------------------- |
| ClOrdID | 11     |  客户端请求唯一标识符 |
| OrderID | 37     |  服务端生成的订单ID |
| OrigClOrdID | 41     |  客户端订单ID |
| Side | 54     | 有效值：1-Buy(买入) 、2-Sell（卖出） |
| Symbol | 55     |  交易对 |
| TransactTime | 60     | 响应消息发送时间(UTC时间)  |
| ExecType | 150     | 执行类型，固定值=6（PENDING_CANCEL） |
| ExecID | 17     | 执行ID，为服务端订单ID |
| OrdStatus | 39     | 订单状态，固定值=4（CANCELED） |
| LeavesQty | 151     | 未成交数量，固定值=0.0 |
| CumQty | 14     | 已成交数量，固定值=0.0 |
| AvgPx | 6     |成交均价，固定值=0.0  |
| Text | 58     |备注  |

撤单失败响应MsgType：

```
OrderCancelReject(9)
```

响应参数:

| 字段名          | 字段Code | 描述                         |
| ------------ | ------ | -------------------------- |
| ClOrdID | 11     |  客户端请求唯一标识符 |
| OrderID | 37     |  服务端生成的订单ID |
| OrigClOrdID | 41     |  客户端订单ID |
| OrdStatus | 39     | 订单状态，固定值=8（REJECTED） |
| CxlRejResponseTo | 434     | 拒绝响应类型，固定值=1（ORDER_CANCEL_REQUEST） |
| CxlRejReason | 14     | 拒绝原因：99-OTHER，0-TOO_LATE_TO_CANCEL ，1-UNKNOWN_ORDER |
| Text | 58     | 失败原因：详细见下  |

各种原因失败时响应参数 Text 中取值：

| 异常text             | 异常描述     |
| ------------------ | -------- |
| order no not exist | 未完成订单不存在 |
| order cancel error | 订单撤销异常   |
| order has execute  | 订单已成交    |
| user not match orderNo | 用户没有操作该订单的权限 |
| wrong order symbol | 错误的交易对 |
| wrong order side | 错误的买卖类型 |
| wrong order number format | 订单号格式错误 |

请求示例:  

```
8=FIX.4.49=17435=F34=2749=7d8f8655-ce10-428d-b10a-b9dcc25b352d52=20190329-03:48:21.70356=GSX11=0b4f59d2-0ffa-44b1-a079-d7b690a7836837=12341=XXX54=155=BTC60=20190329-03:48:21.70310=243
```

返回成功示例: 

```
8=FIX.4.49=18335=834=2749=GSX52=20190329-03:48:21.71456=7d8f8655-ce10-428d-b10a-b9dcc25b352d6=011=XXX14=017=12337=12339=441=*54=155=BTC58=success60=20190329-03:48:21.713150=6151=010=012
```

返回失败示例：

```
 8=FIX.4.49=14035=934=349=GSX52=20190329-03:36:51.82256=7d8f8655-ce10-428d-b10a-b9dcc25b352d11=XXX37=12339=841=*58=order no not exist102=1434=110=201
```

------

#### 3. 信息查询类

##### 3.1 查询未完成订单

功能描述: 

查询未全部成交且未撤销的委托单

请求MsgType:

```
ListStatusRequest(M)
```

接口请求参数:

| 字段名 | 字段Code | 填写类型 | 描述                                                         |
| ------ | -------- | -------- | ------------------------------------------------------------ |
| ListID | 66       | 必填     | 服务端生成的订单ID，多个订单ID请使用 ,  分隔（最多20条） ，*表示查询最近20条下单信息 |

失败响应MsgType:

```
ListStatus(N)
```

失败响应参数:

| 字段名          | 字段Code | 描述                                |
| --------------- | -------- | ----------------------------------- |
| ListID          | 66       | 服务端生成的订单ID，客户端传入的值  |
| ListStatusType  | 429      | 列表状态类型（固定为RESPONSE (2) ） |
| NoRpts          | 82       | 返回列表大小（固定为0）             |
| ListOrderStatus | 431      | 列表中订单的状态(固定为ALERT(5) )   |
| RptSeq          | 83       | 消息序列号(固定为0)                 |
| TotNoOrders     | 68       | 返回列表的总记录条数（固定为1）     |
| NoOrders        | 73       | 未完成订单查询集合（一条记录）      |
| -- ClOrdID      | 11       | 订单号（为 *）                      |
| -- CumQty       | 14       | 已成交数量 （为 0）                 |
| -- OrdStatus    | 39       | 委托状态（固定为STOPPED (7) ）      |
| -- LeavesQty    | 151      | 剩余未成交数量 （固定为0）          |
| -- CxlQty       | 84       | 订单撤销数量（固定为0）             |
| -- AvgPx        | 6        | 订单成交均价（固定为0）             |
| -- Text         | 58       | 错误消息提示                        |
| TransactTime    | 60       | 响应消息发送时间(UTC时间)           |

成功响应MsgType:

```
ListStatus(N)
```

成功响应参数:

| 字段名          | 字段Code | 描述                                                |
| --------------- | -------- | --------------------------------------------------- |
| ListID          | 66       | 服务端生成的订单ID，客户端传入的值                  |
| ListStatusType  | 429      | 列表状态类型（固定为RESPONSE (2) ）                 |
| NoRpts          | 82       | 返回列表大小                                        |
| ListOrderStatus | 431      | 列表中订单的状态(固定为EXECUTING (3) )              |
| RptSeq          | 83       | 消息序列号(固定为0)                                 |
| TotNoOrders     | 68       | 返回列表的总记录条数                                |
| NoOrders        | 73       | 未完成订单查询集合                                  |
| -- ClOrdID      | 11       | 订单号                                              |
| -- CumQty       | 14       | 已成交数量                                          |
| -- OrdStatus    | 39       | 委托状态（PENDING_NEW (A) / PARTIALLY_FILLED (1) ） |
| -- LeavesQty    | 151      | 剩余未成交数量 （CumQty+LeavesQty=下单时总数量）    |
| -- CxlQty       | 84       | 订单撤销数量                                        |
| -- AvgPx        | 6        | 订单成交均价                                        |
| TransactTime    | 60       | 响应消息发送时间(UTC时间)                           |

请求示例：

```
sessionId=FIX.4.4:GSX->7d8f8655-ce10-428d-b10a-b9dcc25b352d, message:8=FIX.4.49=16835=M34=249=7d8f8655-ce10-428d-b10a-b9dcc25b352d50=162088347629577420152=20190404-06:59:47.20356=GSX66=1620883476295774201,1620883476295774202,162088347629577420310=243 
```

失败返回示例：

```
8=FIX.4.49=25135=N34=249=GSX52=20190404-07:15:57.77156=7d8f8655-ce10-428d-b10a-b9dcc25b352d60=20190404-07:15:57.77166=1620883476295774201,1620883476295774202,162088347629577420368=082=083=0429=2431=573=111=*14=039=7151=084=06=058=order not exist10=081
```

成功返回示例：

```
8=FIX.4.49=35435=N34=5049=GSX52=20190404-07:38:38.88956=7d8f8655-ce10-428d-b10a-b9dcc25b352d60=20190404-07:38:38.88866=1620883476295774201,1620883476295774202,162088347629577420368=382=383=0429=2431=373=311=162088347629577420114=839=1151=284=06=0.511=162088347629577420214=1039=A151=184=06=0.311=162088347629577420314=539=A151=384=06=0.210=199
```

各种原因失败时响应参数 Text 中取值：

| 异常text             | 异常描述                                           |
| -------------------- | -------------------------------------------------- |
| user not exist       | 错误的accesskey                                    |
| orderList is invalid | 订单ID无效（传入订单ID多于20条或者传入的是非法ID） |
| order not exist      | 未完成订单不存在                                   |


##### 3.2 查询行情数据

功能描述: 

查询行情数据，包括深度行情、实时行情、5分钟行情，通过NoMDEntryTypes集合进行区分行情类型

请求MsgType:

```
MarketDataRequest(V)
```

接口请求参数:

| 字段名     | 字段Code | 填写类型 | 描述                         |
| ------- | ------ | ---- | -------------------------- |
| MDReqID | 262    | 必填   | 请求ID |
| SubscriptionRequestType | 263    | 必填   | 订阅类型：有效值0（snapshot） |
| MarketDepth | 264    | 必填   | 获取记录数，有效值： 0-N（最大值根据各个行情数据配置而定）。如果查询交易对最后成交时间，该值只能为1。 |
| NoMDEntryTypes | 267    | 必填   | 行情数据类型集合 |
| -- MDEntryType | 269    | 必填   | 有效值：</br>1、深度行情[ 0-BID、1-OFFER] ;</br>2、实时行情[2-TRADE];</br> 3、k线数据[4-OPENING_PRICE、 5-CLOSING_PRICE</br>、7-TRADING_SESSION_HIGH_PRICE</br>、8-TRADING_SESSION_LOW_PRICE、B-TRADE_VOLUME] </br>4、价格行情[ 0-BID、1-OFFER、6-SETTLEMENT_PRICE] ; </br>5、最后成交时间[ 0-BID、1-OFFER、2-TRADE] ;|
| NoRelatedSym | 146    | 必填   | 交易对信息集合，只能设置为1，即只能查询一个交易对的行情数据 |
| -- Symbol | 55    | 必填   | 交易对，例如：BTC/USDT |
| ApplQueueMax | 812    | 非必填   | 如果是查询k线数据，该值必填，该值表示分钟数，有效值：1、5、15、30、60(1h)、120(2h)、240(4h)、360(6h)、720(12h)、1440(1d)、10080(1周); |

成功响应MsgType:

```
MarketDataSnapshotFullRefresh(W)
```

响应参数:

| 字段名     | 字段Code | 填写类型 | 描述                         |
| ------- | ------ | ---- | -------------------------- |
| MDReqID | 262    | 必填   | 请求ID |
| Symbol | 55    | 必填   | 交易对，例如：BTC/USDT |
| NoMDEntryTypes | 267    | 必填   | 行情数据类型集合 |
| -- MDEntryType | 269    | 必填   | 有效值 0-BID（买入价）、1-OFFER（卖出价）、 </br>4-OPENING_PRICE（开盘价）、 5-CLOSING_PRICE（收盘价）、</br>7-TRADING_SESSION_HIGH_PRICE（最高价）、</br>8-TRADING_SESSION_LOW_PRICE（最低价）、B-TRADE_VOLUME(交易量) |
| -- MDEntryPx | 270    | 必填   | 价格 |
| -- MDEntrySize | 271    | 选填   | 数量 |
| -- MDEntryPositionNo | 290    | 必填   | 序号(从1开始) |
| -- MDEntryDate | 272    | 选填   |日期  |
| -- MDEntryTime | 273    | 选填   | 时间 |

请求示例:

```
8=FIX.4.49=13335=V34=249=7d8f8655-ce10-428d-b10a-b9dcc25b352d52=20190401-08:17:59.49656=GSX262=RQ001263=0264=0146=155=XRP/BTC267=1269=210=192
```

返回成功示例: 


```
8=FIX.4.4 TODO
```
拒绝响应MsgType:

```
MarketDataRequestReject(Y)
```

响应参数:

| 字段名     | 字段Code | 填写类型 | 描述                         |
| ------- | ------ | ---- | -------------------------- |
| MDReqID | 262    | 必填   | 请求ID |
| MDReqRejReason | 281    | 必填 | 拒绝原因：</br>8-UNSUPPORTED_MDENTRYTYPE（行情数据类不支持）、</br>4-UNSUPPORTED_SUBSCRIPTIONREQUESTTYPE（订阅类型不支持）、 </br>0-UNKNOWN_SYMBOL（交易对错误）</br> （为空的时候，具体原因看Text字段） |
| Text | 58    | 选填   | 备注 |

返回失败示例: 

```
8=FIX.4.49=11935=Y34=249=GSX52=20190401-08:18:03.43856=7d8f8655-ce10-428d-b10a-b9dcc25b352d58=symbol is invalid262=RQ001281=010=032
```

各种原因失败时响应参数 Text 中取值：

| 异常text             | 异常描述     |
| ------------------ | -------- |
| only supported subscription request type values:0=Snapshot | 订阅类型只支持0 |
| only one symbol is allowed | 只允许查询一个交易对的行情数据 |
| the parameter 'MarketDepth' can only be 1 | 当查询交易对最后一个成交时间时，MarketDepth只能为1 |
| the market data entry types is error | 行情类型错误 |
| no market data | 没有查询到行情数据 |
| symbol is invalid | 无效的交易对 |