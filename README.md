
## API routes

#### Login

```http
  Post http://localhost:8080/api/v2/auth
```
+ Request (application/json)

    + Body

            {
              "email": "personal@email.com",
              "pass": "somehashorsomething123"
            }

+ Response 200 (application/json)

    + Body

            {
                "Token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.
                eyJhcHAiOiIzNDhlNTM4MzQ2M2Y3MzZjMGExZTJhNTFmNjYwZjA5NCIsInN1YmRvbWluaW8iOiJleGVtcGxvIiwiY2xpZW50IjoiMTU0ZDZlZGQ4YmQzMDEwYzQ4NjBkN2E5Y
                zk1NzNmYmVmZTUyNGRlZiJ9.JJNs0bFtGOtwyJy_r-eefsvkd387M_x7zpucE1m4WIw"
            }

<br/>
<br/>

## API FEATURES

### Audit

  + Auditing tables for each Entity in the application using Hibernate Envers
  
  <br/>

  The auditing was made using Hibernate Envers dependency version 5.5.7.FINAL

  ```xml
  <dependency>
		<groupId>org.hibernate</groupId>
		<artifactId>hibernate-envers</artifactId>
		<version>5.5.7.Final</version>
  </dependency>
  ```
  <br/>

  The Model class using Auditing features will need to have the annotation 
  ```java 
  @Audited
  ```
  <br/>
  Example Model

  ```java 
  @Entity
  @Table(name = "TB_EXAMPLE")
  @Audited //required annotation for create audit table
  //@Other annotations
  public class ExampleModel extends AbstractModel {
    //attribute fields, getter and setters etc
  }
  ```
<br/>

## SECURITY

 + Using Spring Security with JWT authentication

 + Storing sensitive information such as the secret key, expiration time, and maximum login attempts in the application.properties file.

 ***IMPORTANT***
 
 + When in production, go to ```JWTConfig``` class, ```filterChain ```  method and remove the line disabling the Cross-Site Request Forgery (CSRF)

 ```java
   @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        
        http
            .csrf(csrf -> csrf.disable())//Remove this line when in production mode
            .authorizeHttpRequests(auth -> auth
            .antMatchers(HttpMethod.POST, "/api/v2/auth").permitAll()
            .antMatchers("/api/v2/common/**").permitAll()
            .anyRequest().authenticated()
        );

        //Rest of the code
    }
        
 ```

  <br>

  + ### <ins>Authentication token generation</ins>
    + The JWT authentication token will be generated successfully under the following conditions:
        - The user IS ACTIVE.
        - The company associated with the user account IS ACTIVE
        - The number of login attempts for the user does not exceed the defined limit (maximum of 3 attempts).
    
    <br/>

    + By satisfying these conditions, a valid JWT token is generated, allowing the user to make authorized requests to the system's API endpoints securely.

    + Additionally, the JWT token has an expiration time, typically set to a specific duration, after which it becomes invalid and requires reauthentication
 







  





