# Graduation-Design
毕业设计

### 有关jar包

1. 由于某些原因，打包会有重复文件导致签名问题，需要删除 META-INF下的*.SF,*.DSA,*.RSA文件
2. jdbcDriver问题，代码要加入Class.forName("com.mysql.jdbc.Driver")，同时在连接的数据库配置中加入Driver：connectionProperties.put("driver","com.mysql.cj.jdbc.Driver")





