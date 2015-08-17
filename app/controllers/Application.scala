package controllers

import play.api._
import play.api.mvc._
import models.ApplicationModel
import scala.io.Source
import scala.util.control._
import java.io.PrintWriter
import models.UserAreaModel

class Application extends Controller {

  def index = Action {implicit request =>
    val latestJobs = ApplicationModel.readLatestFiveJobs()
    Ok(views.html.home(views.html.header("Home"),latestJobs))
  }
  
  
  def searchResult = Action { implicit request =>   
//    testFunction
    val requestData : Map[String,Any] = Map()
    val searchQuery = request.body.asFormUrlEncoded.getOrElse(requestData).getOrElse("searchQuery",requestData)
                      .toString().stripPrefix("ArrayBuffer(").stripSuffix(")").trim()
    val sql = GeneralFunctions.searchQueryGenerator(searchQuery.toLowerCase())
//    Ok(sql)
    var resultSet = ApplicationModel.readJobByQuery(sql)
    var locationMap : Map[Int,String] = Map()    
    var skillMap : Map[Int,String] = Map()
    var jobIdList : List[Int] = List()
    for(job <- resultSet){
      val jobid =job.get("jobtable.jobid").get.toString().toInt
      if(jobIdList.contains(jobid)){
        resultSet = resultSet diff List(job) 
      }else{        
    	  jobIdList = (jobid) :: jobIdList
      }
      val locationSet = ApplicationModel.readLocationOfJob(jobid)
      val skillSet = ApplicationModel.readSkillOfJob(jobid)
      var locationString : String = GeneralFunctions.getLocationStringFromMap(locationSet)
      var skillString : String = GeneralFunctions.getSkillStringFromMap(skillSet)
      locationMap += (jobid -> locationString)
      skillMap += (jobid -> skillString)
    }
    Ok(views.html.searchResult(views.html.header("Job Search Result"),resultSet, locationMap, skillMap))
  }
  
  
  def login = Action { implicit request =>
    Ok(views.html.login(views.html.header("Login")))    
  }
  
  def candidateLogin(hashId : String) = Action { implicit request =>
    val userAreaInstance : UserArea = new UserArea
    if(userAreaInstance.checkSession(request)){      
      Redirect(routes.UserArea.dashboard).withCookies(Cookie("jobToApply", hashId))
    }
    else{
    	Ok(views.html.candidateLogin(views.html.header("Login"))).withCookies(Cookie("jobToApply", hashId))      
    }
  }
  
  def companyRegistration = Action { implicit request =>
    Ok(views.html.companyRegistration(views.html.header("Company registration")))
  }
  
  def feedback = Action { implicit request =>
    Ok(views.html.feedback(views.html.header("Feedback")))
  }
  
  def candidateRegistration = Action { implicit request =>
    Ok(views.html.candidateRegistration(views.html.header("Candidate Registration")))
  }
  
  def doCandidateRegistration = Action(parse.multipartFormData) { implicit request =>
    try{
      val name = request.body.asFormUrlEncoded.get("name").get.head
      val email = request.body.asFormUrlEncoded.get("email").get.head
      val mobile = request.body.asFormUrlEncoded.get("contact_no").get.head
      val password = request.body.asFormUrlEncoded.get("password").get.head
      val confirm_password = request.body.asFormUrlEncoded.get("confirm_password").get.head
      val location = request.body.asFormUrlEncoded.get("location").get.head
      val experience = request.body.asFormUrlEncoded.get("experience").get.head
      if(password!=confirm_password){
        Redirect(routes.Application.candidateRegistration).flashing(
                "error" -> "Password Doesnot Match")
      }
      else{ 
    	  var filename : String = ""
  			request.body.file("resume").map { resume =>
  			  import java.io.File
  			  filename = mobile+resume.filename
  			  val contentType = resume.contentType
  			  resume.ref.moveTo(new File("public/resume/"+filename),true)        
        }
        val result = ApplicationModel.saveCandidate(name, email, mobile, password, location, experience.toInt, filename)
    	  if(result>0){
    		  Redirect(routes.Application.login).flashing(
    				  "success" -> "Your Registration Completed succesfully, Please login to proceed")
    	  }else{
    		  Redirect(routes.Application.candidateRegistration).flashing(
    				  "error" -> "Candidate Registration failed, Some Error occured, Please try again")
    	  }
      }
    }  
    catch{
      case ex : Exception =>{
        Logger.error("Candidate Registration failed  : " + ex.getMessage)
        Redirect(routes.Application.candidateRegistration).flashing(
                "error" -> "Candidate Registration failed, Check your input data")
      }      
    }
  }
  
  def doCompanyRegistration = Action { implicit request =>
    try{
      val name = request.body.asFormUrlEncoded.get.get("name").get.head
      val email = request.body.asFormUrlEncoded.get.get("email").get.head
      val mobile = request.body.asFormUrlEncoded.get.get("contact_no").get.head
      val password = request.body.asFormUrlEncoded.get.get("password").get.head
      val confirm_password = request.body.asFormUrlEncoded.get.get("confirm_password").get.head
      val address = request.body.asFormUrlEncoded.get.get("address").get.head
      if(password!=confirm_password){
        Redirect(routes.Application.companyRegistration).flashing(
                "error" -> "Password Doesnot Match")
      }
      else{
     	  val result = ApplicationModel.saveCompany(name, email, mobile, password, address)
			  if(result>0){
				  Redirect(routes.Application.login).flashing(
						  "success" -> "Your Registration Completed succesfully, Please login to proceed")
			  }else{
				  Redirect(routes.Application.companyRegistration).flashing(
						  "error" -> "Company Registration failed, Some Error occured, Please try again")
			  }
      }
    }  
    catch{
      case ex : Exception =>{
        Logger.error("Candidate Registration failed  : " + ex.getMessage)
        Redirect(routes.Application.candidateRegistration).flashing(
                "error" -> "Candidate Registration failed, Check your input data")
      }      
    }
  }
  
  def processFeedback = Action { implicit request =>
    try{
      
      val name = request.body.asFormUrlEncoded.get.get("name").get.head
      val email = request.body.asFormUrlEncoded.get.get("email").get.head
      val mobile = request.body.asFormUrlEncoded.get.get("contact_no").get.head
      val subject = request.body.asFormUrlEncoded.get.get("subject").get.head
      val message = request.body.asFormUrlEncoded.get.get("message").get.head
      
      val result = ApplicationModel.saveFeedback(name, email, mobile, subject, message)
      if(result>0){
        Redirect(routes.Application.feedback()).flashing(
                "success" -> "Your Feedback submitted successfully")
      }
      else{
        Redirect(routes.Application.feedback()).flashing(
                "error" -> "Your Feedback submition error, Please try again")
      }
    }
    catch{
      case ex : Exception =>{
        Logger.error("Processing Feedback failed  : " + ex.getMessage)
        Redirect(routes.Application.feedback()).flashing(
                "error" -> "Processing feedback attempt is failed, Check your input data")
      }      
    }
  }
  
  def testFunction={
    var viewContent = ""
    for(line <- Source.fromFile("public/corpus/states").getLines()){
      viewContent += line.toLowerCase()+"""
"""
    }   
    viewContent.trim()
    val writer = new PrintWriter("public/corpus/states")
    writer.write(viewContent)
    writer.close()
  }
}