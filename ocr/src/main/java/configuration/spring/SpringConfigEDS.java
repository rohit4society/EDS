package configuration.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.tihor.ocr.utils.*"})
public class SpringConfigEDS
{
	
	
 /*@Bean // or @Bean(name = "nameOfYourBean")
 public ResourcePool getResourcePool() 
 { 
     return new ResourcePool(); 
 }*/

}