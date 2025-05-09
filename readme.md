# 开放平台demo

先将`resources/application.yml`里的配置改为实际生产环境的值。

## 获取授权
接口: POST  /open/token

可以参考： com.imdealone.open.demo.controller.AuthController

## 上传图片
接口: PUT /open/object
模拟请求的时候，需要先调用**获取授权**接口拿到token后放到header里，细节可以查阅代码。

可以参考: com.imdealone.open.demo.controller.ObjectController

