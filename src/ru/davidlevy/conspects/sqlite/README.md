https://www.ibm.com/developerworks/ru/library/dm-0802tiwary/index.html

SQLite does not support Stored Procedures!
```java 
String sql = "CREATE procedure createProductDef(productname   VARCHAR(64), \n" ....
preparedStatement = connection.prepareStatement(sql);
preparedStatement.executeUpdate();

String sqlCall = "{call CreateProductDef(?,?,?,?,?)}";
CallableStatement callableStatement = connection.prepareCall(sqlCall);
callableStatement.setString("productname", "Name");     // Set Product Name.
callableStatement.setString("productdesc", "Description");     // Set Product Description.
callableStatement.setFloat("listprice", 111.1f);   // Set Product ListPrice.
callableStatement.setFloat("minprice", 0.1f);     // Set Product MinPrice.

callableStatement.registerOutParameter("prod_id", Types.FLOAT);
callableStatement.execute();

float id = callableStatement.getFloat("prod_id");
```