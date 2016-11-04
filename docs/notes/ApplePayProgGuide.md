# Apple pay programming guide
[> home page link](https://developer.apple.com/library/content/ApplePay_Guide/index.html)

## Getting Started
### working with Apple pay
1. 生成支付请求
2. 显示支付验证画面
3. callback to authorization delegate
4. Apple Pay 通过本机的Secure Element加密支付请求
5. Secure Element 添加卡和商家信息，然后生成token
6. Secure Element 发送token到Apple服务器，再次使用商家的密钥凭证加密
7. 发送回加密后的token到本机
8. 发送加密后的token到支付提供商的服务器，支付提供商负责解密
9. 或者发送到商家自己的服务器，自己负责解密，然后把解密后的数据，通过现有的支付系统接口发送给支付提供商

### Testing Apple Pay Transactions
通过Sandbox环境测试
1. 在iTunes Connect里创建测试账户，这个账户可以测试Apple Pay和IAP
2. 使用测试账户登录测试手机iCloud
3. 添加普通的信用卡到wallet

> * 登入登出iCloud会删除所有的卡
> * 不要发送数据给支付提供商

### Configuring Your Environment
1. 注册商户ID
2. 上传密钥凭证
3. Xcode功能打开

## Working with Payment Requests
### Create Requests
1. `canMakePayments` 检查物理支持
2. `canMakePaymentsUsingNetworks:` 检查支付信用卡可用
3. `openPaymentSetup` 打开支付设置
4. 设置Currency, Country, merchantId
5. 添加备注信息，如送货方式，折扣比例，总价
6. 设置支付网络以及支付安全标准，如Master，Visal，3DS，EVM
7. 最后一个item为成交价
> * `NSDecimalNumber` 不能在商业系统中简单使用float或者double，浮点运算会带来不准确的结果
> * `applicationData` 定制消息

###　Authorizing Payments
1. `PaymentViewController`做两件事情：
  * 提供支付相关选项给用户
  * 验证用户身份
2. `PKPaymentAuthorizationController`不依赖于UIKit，其他与`PKPaymentAuthorizationViewController`基本相同
3. `paymentAuthorizationViewController:didAuthorizePayment:completion:`只有当通过验证之后才会生成token
4. `paymentAuthorizationViewControllerDidFinish:`完成验证之后需要手动关闭，进入自定义的画面，如果感谢画面

### Processing Payments
1. 发送支付信息包含其他商品信息
2. 通过hash和signature验证数据的可靠性
3. 解密加密后的数据
4. 提交支付信息到支付服务商服务器
5. 提交订单信息到自己的订单管理系统

### PKPaymentToken
PKPaymentToken
* Transaction id
* Payment network
* paymentData(JSON Dictionary)
  * Signature
  * Header
  * Encrypted Data

[Formatter & Encryption link](https://developer.apple.com/library/content/documentation/PassKit/Reference/PaymentTokenJSON/PaymentTokenJSON.html#//apple_ref/doc/uid/TP40014929)
