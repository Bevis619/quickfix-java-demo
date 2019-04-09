1. 登陆接口
- url： http://localhost:9092/order/logon
- method: post 
- body:
   
```
{
	"encryptMethod":"0",
	"heartBtInt":30,
	"rawData":"7210a93581c887d1bf6c96d0dd06bde0",
	"rawDataLength":32
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
	"clOrdID":"1223",
	"symbol":"BTC/USD",
	"price":0,
	"side":"1",
	"ordType":"1",
	"orderQty":0,
	"cashOrderQty":11
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

7. 查询行情：TODO