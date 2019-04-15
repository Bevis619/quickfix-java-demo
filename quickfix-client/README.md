1. 登陆接口
- url： http://localhost:9092/order/logon
- method: post 
- body:
   
```
{
    "senderCompID": "",
    "secretkey": "",
    "encryptMethod": "0",
    "heartBtInt": 30,
    "rawData": "",
    "rawDataLength": 0
}
```

2. 注销接口
- url: http://localhost:9092/order/logout
- method: get

3. 心跳消息
- url: http://localhost:9092/order/heartbeat
- method: get

4. 创建订单
- url: http://localhost:9092/order/new
- method: post
- body:

```
{
	"clOrdID":"12234",
	"symbol":"BONDS111/STACS",
	"price":12,
	"side":"2",
	"ordType":"2",
	"orderQty":20,
	"cashOrderQty":0
}
```


5. 撤销订单
- url: http://localhost:9092/order/cancel
- method: post
- body:

```
{
	"orderID":"11111",
	"clOrdID":"123",
	"origClOrdID":"123",
	"symbol":"BTC/USD",
	"side":"1"
}
```

6. 查询订单
- url: http://localhost:9092/order/queryOrder
- method: post
- body:

```
{
	"listID":"123,345"	
}
```

7. 查询行情
- url：http://localhost:9092/order/market/data
- method: post
- body:

```
查询深度行情数据，对应之前api  depth接口
{
    "mdReqID": "123",
    "subscriptionRequestType": "0",
    "marketDepth": "10",
    "noMdEntryTypes": [
        {
            "mdEntryType": "0"
        },
        {
            "mdEntryType": "1"
        }
    ],
    "noRelatedSymbols": [
        {
            "symbol": "BTC/USD"
        }
    ]
}
```

```
查询实时行情数据，对应之前api  tickers接口
{
    "mdReqID": "123",
    "subscriptionRequestType": "0",
    "marketDepth": "10",
    "noMdEntryTypes": [
        {
            "mdEntryType": "2"
        }
    ],
    "noRelatedSymbols": [
        {
            "symbol": "BTC/USD"
        }
    ]
}
```

```
查询分时行情数据-5分钟K线数据，对应之前api  kline接口
{
    "mdReqID": "123",
    "subscriptionRequestType": "0",
    "marketDepth": "5",
    "noMdEntryTypes": [
        {
            "mdEntryType": "4"
        },
        {
            "mdEntryType": "5"
        },
        {
            "mdEntryType": "7"
        },
        {
            "mdEntryType": "8"
        },
        {
            "mdEntryType": "B"
        }
    ],
    "noRelatedSymbols": [
        {
            "symbol": "BTC/USD"
        }
    ]
}
```


8.TOD