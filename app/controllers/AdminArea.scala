package controllers

import play.api._
import play.api.mvc._
import models.AdminAreaModel

/**
 * @author qburst
 */
class AdminArea extends Controller{
  
  def checkSession (request : Request[AnyContent]) : Boolean = {
    request.session.get("userid").map { user =>
      val userType = request.session.get("usertype").get
//      println(group)
      if(userType.toInt==1){        
        return true
      }
      else{
        return false
      }
    }.getOrElse {
      return false
    } 
  }
  
  def dashBoard = Action{implicit request =>
    if(checkSession(request)){      
    	Ok(views.html.admin.adminHeader("Admion DashBoard",1))    
    }
    else{
      Logger.error("Invalid Session, loading failed from admin dashboard ")
        Redirect(routes.Application.index).flashing(
                "error" -> "You Dont have permission to load this page, try again latera")
    }
  }
  
  def newCompany = Action { implicit request =>
    if(checkSession(request)){      
    	val companyList = AdminAreaModel.readCompanyList
    	Ok(views.html.admin.newCompany(views.html.admin.adminHeader("Create New Company",2),companyList))   
    }
    else{
      Logger.error("Invalid Session, loading failed from admin newcompany ")
        Redirect(routes.Application.index).flashing(
                "error" -> "You Dont have permission to load this page, try again latera")
    }
  }
  
  def newJob = Action { implicit request =>
    if(checkSession(request)){      
    	val companyList = AdminAreaModel.readCompanyList
    	Ok(views.html.admin.newJob(views.html.admin.adminHeader("Create new Job",2),companyList))
    }
    else{
      Logger.error("Invalid Session, loading failed from admin newJob ")
        Redirect(routes.Application.index).flashing(
                "error" -> "You Dont have permission to load this page, try again latera")
    }
  }
  
  def jobList = Action { implicit request =>
    if(checkSession(request)){      
    	val jobList = AdminAreaModel.readJobList
    	Ok(views.html.admin.jobList(views.html.admin.adminHeader("Job List",2),jobList))
    }
    else{
      Logger.error("Invalid Session, loading failed from admin jobList ")
        Redirect(routes.Application.index).flashing(
                "error" -> "You Dont have permission to load this page, try again latera")
    }
  }
  
  def feedbackList = Action { implicit request =>
    if(checkSession(request)){      
    	val feedbackList = AdminAreaModel.readFeedbackList
    	Ok(views.html.admin.feedbackList(views.html.admin.adminHeader("Feedback List",3),feedbackList))
    }
    else{
      Logger.error("Invalid Session, loading failed from admin feedbackList ")
        Redirect(routes.Application.index).flashing(
                "error" -> "You Dont have permission to load this page, try again latera")
    }
  }
  
  def jobViewInDetail(hashId :  String) = Action { implicit request =>
    if(checkSession(request)){      
    	val jobData = AdminAreaModel.readSingleJob(hashId)
			val jobLocation = GeneralFunctions.getLocationStringFromMap(jobData)
			val jobSkill = GeneralFunctions.getSkillStringFromMap(jobData)
			Ok(views.html.admin.jobViewInDetail(views.html.admin.adminHeader("Job View in Detail",2),jobData.head,jobLocation,jobSkill))
    }
    else{
      Logger.error("Invalid Session, loading failed from admin jobViewInDetail ")
        Redirect(routes.Application.index).flashing(
                "error" -> "You Dont have permission to load this page, try again latera")
    }
  }
  
  def feedbackInDetail(hashId : String ) = Action { implicit request =>
    if(checkSession(request)){      
    	val feedback = AdminAreaModel.readSingleFeedBack(hashId)
			Ok(views.html.admin.feedbackInDetail(views.html.admin.adminHeader("Feedback Detailed View",3),feedback))
    }
    else{
      Logger.error("Invalid Session, loading failed from admin feedbackInDetail")
        Redirect(routes.Application.index).flashing(
                "error" -> "You Dont have permission to load this page, try again latera")
    }
  }
  
  def saveNewCompany = Action { implicit request =>
    try{
      if(checkSession(request)){        
    	  val companyName = request.body.asFormUrlEncoded.get.get("name").get.head
			  val companyAddress = request.body.asFormUrlEncoded.get.get("address").get.head
			  val result = AdminAreaModel.saveNewCompany(companyName, companyAddress)
			  if(result>0){
				  Redirect(routes.AdminArea.newCompany).flashing(
						  "success" -> "Company Created Successfully")
			  }
			  else{        
				  Redirect(routes.AdminArea.newCompany).flashing(
						  "error" -> "Company Creation Failed")
			  }
      }
      else{
        Logger.error("Invalid Session, loading failed from admin saveNewCompany ")
          Redirect(routes.Application.index).flashing(
                  "error" -> "You Dont have permission to load this page, try again latera")
      }
    }
    catch{
      case ex : Exception =>{
        Logger.error("Company Creation Attempt failed : "+ex.getMessage)
        Redirect(routes.AdminArea.newCompany).flashing(
                "error" -> "Company Creation attempt failed, Check your input data")
      }        
    }
  }
  
  
  def saveNewJob = Action { implicit request =>
    try{
      if(checkSession(request)){        
    	  val job = request.body.asFormUrlEncoded.get.get("job").get.head
			  val company = request.body.asFormUrlEncoded.get.get("company").get.head
			  val description = request.body.asFormUrlEncoded.get.get("description").get.head
			  val experience = request.body.asFormUrlEncoded.get.get("experience").get.head.toInt
			  val salary = request.body.asFormUrlEncoded.get.get("salary").get.head.toFloat
			  val location = request.body.asFormUrlEncoded.get.get("location").get.head
			  val skills = request.body.asFormUrlEncoded.get.get("skills").get.head
			  val jobid = AdminAreaModel.saveNewJob(job, company, description, salary, experience)
			  if(jobid > 0){
				  val skillList = skills.split(",").toList
						  var updatedSkills : Int = 0
						  val locationList = location.split(",").toList
						  var updatedLocations : Int = 0
						  for(skill <- skillList){
							  updatedSkills += AdminAreaModel.saveJobSkill(jobid, skill)
						  }
						  if(updatedSkills == skillList.size){
							  for(loc <- locationList){
								  updatedLocations += AdminAreaModel.saveJobLocation(jobid, loc)
							  }
							  if(updatedLocations != locationList.size){
								  Redirect(routes.AdminArea.newJob).flashing(
										  "error" -> "Error Occured While Updating locations, But Partially updated")
							  }
						  }
						  else{
							  Redirect(routes.AdminArea.newJob).flashing(
									  "error" -> "Error Occured While Updating skills")
						  }
						  Redirect(routes.AdminArea.newJob).flashing(
								  "success" -> "job Created Successfully")
			  }
			  else{        
				  Redirect(routes.AdminArea.newJob).flashing(
						  "error" -> "Job Creation Failed")
			  }
      }
      else{
        Logger.error("Invalid Session, loading failed from admin saveNewJob")
          Redirect(routes.Application.index).flashing(
                  "error" -> "You Dont have permission to load this page, try again latera")
      }
    }
    catch{
      case ex : Exception =>{
        Logger.error("Company Creation Attempt failed : "+ex.getMessage)
        Redirect(routes.AdminArea.newJob).flashing(
                "error" -> "Job Creation attempt failed, Check your input data")
      }        
    }
  }
  
}