package controllers


import play.api._
import play.api.mvc._
import models.ApplicationModel
import models.LoginModel

/**
 * @author qburst
 */
class Login extends Controller {
  

  def doLogin = Action {implicit request =>    
    try{      
    	val requestData : Map[String,Any] = Map()
			val username = request.body.asFormUrlEncoded.get.get("username").get.head
			val password = request.body.asFormUrlEncoded.get.get("password").get.head
			val result = LoginModel.checkUser(username,password)
      println(result)
			if(!result.isEmpty){        
				if(result.head._2==1){
					Logger.info("Loggin successfull, Redirecting to admin Page")
					Redirect("/adminArea").withSession(
							request.session + ("user" -> username)
							+ ("userid" -> result.head._1.toString() )
							+ ("usertype" -> result.head._2.toString() )
							)
				}
        else if(result.head._2==2){
          Logger.info("Loggin successfull, Redirecting to user Page ")
          Redirect("/userArea").withSession(
              request.session + ("user" -> username)
              + ("userid" -> result.head._1.toString() )
              + ("usertype" -> result.head._2.toString() )
              )
        }
        else if(result.head._2==3){
          Logger.info("Loggin successfull, Redirecting to Company Page ")
          Redirect("/companyArea").withSession(
              request.session + ("user" -> username)
              + ("userid" -> result.head._1.toString() )
              + ("usertype" -> result.head._2.toString() )
              )
        }
				else{
					Logger.error("Loggin Attempt Failed : Invalid User Details")
					Redirect(routes.Application.login()).flashing(
							"error" -> "Invalid Login Attempt")
				}
			}
			else{
				Ok("Invalid Username or Password")
			}
    }
    catch{
      case ex : Exception =>{
        Logger.error("Loggin Attempt Failed : "+ex.getMessage)
        Redirect(routes.Application.login()).flashing(
                "error" -> "Invalid Login Attempt")
      }
        
    }
    /*loginForm.bindFromRequest.fold(
    formWithErrors => {
      BadRequest(views.html.main("Login Error",formWithErrors))
    },
    loginData => {
    })*/
  }
  
  
  def logout = Action { implicit request =>
    Redirect("/").withSession(
        request.session - "userid" - "usertype"   
    ) 
  }

}