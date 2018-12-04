package hello;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.code.kaptcha.impl.DefaultKaptcha;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    
    @Autowired
	private CustomerRepository repository;
    
    @Autowired
	DefaultKaptcha defaultKaptcha;

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World000") String name) {
		//repository.save(new Customer("Cheng", "Wang"));
        return new Greeting(counter.incrementAndGet(),
                            String.format(template, name));
    }
    
    @RequestMapping("/customer/{name}")
    public Customer queryCustomer(@PathVariable("name") String name){
    	Customer customer = repository.findByFirstName(name);
    	System.out.println(customer);
    	return customer;
    }
    
    @RequestMapping("/customers")
    public List<Customer> queryCustomers(){
    	List<Customer> customers = repository.findByLastName("Smith");
   // 	System.out.println(customers);
    	return customers;
    }
    
    @RequestMapping("/customerpage")
    public Page<Customer> queryCustomerspage(@PageableDefault(size = 5, sort = {"FirstName"}, direction = Sort.Direction.DESC) Pageable pageable){
    	
    	Page<Customer> customers = repository.findByLastName("Smith",pageable);
    	return customers;
    }
    
    
    @RequestMapping("/defaultKaptcha")
	public void defaultKaptcha(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse) throws Exception{
		 	byte[] captchaChallengeAsJpeg = null;  
	         ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();  
	         try {  
	         //������֤���ַ��������浽session��
	         String createText = defaultKaptcha.createText();
	         httpServletRequest.getSession().setAttribute("vrifyCode", createText);
	         //ʹ����������֤���ַ�������һ��BufferedImage����תΪbyteд�뵽byte������
             BufferedImage challenge = defaultKaptcha.createImage(createText);
             ImageIO.write(challenge, "jpg", jpegOutputStream);
	         } catch (IllegalArgumentException e) {  
	             httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);  
	             return;  
	         } 
	   
	         //����response�������Ϊimage/jpeg���ͣ�ʹ��response��������ͼƬ��byte����
	         captchaChallengeAsJpeg = jpegOutputStream.toByteArray();  
	         httpServletResponse.setHeader("Cache-Control", "no-store");  
	         httpServletResponse.setHeader("Pragma", "no-cache");  
	         httpServletResponse.setDateHeader("Expires", 0);  
	         httpServletResponse.setContentType("image/jpeg");  
	         ServletOutputStream responseOutputStream =  
	                 httpServletResponse.getOutputStream();  
	         responseOutputStream.write(captchaChallengeAsJpeg);  
	         responseOutputStream.flush();  
	         responseOutputStream.close();  
	}

    
}
