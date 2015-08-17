package controllers

import play.api._
import play.api.mvc._
import play.Logger
import models.CompanyAreaModel
import models.AdminAreaModel
import models.UserAreaModel


/**
 * @author qburst
 */
class CompanyArea extends Controller {
  
  def checkSession (request : Request[AnyContent]) : Boolean = {
    request.session.get("userid").map { user =>
      val userType = request.session.get("usertype").get
      if(userType.toInt==3){        
        return true
      }
      else{
        return false
      }
    }.getOrElse {
      return false
    } 
  }
    
  def dashboard = Action { implicit request =>
    if(checkSession(request)){
      Ok(views.html.company.companyHeader("Dashboard",1))
    }
    else{
      Logger.error("Invalid Session, loading failed from company dashboard ")
      Redirect(routes.Application.index).flashing(
              "error" -> "You Dont have permission to load this page, try again later")
    }
  }
  
  def newJob = Action { implicit request =>
    if(checkSession(request)){
      val companyName = CompanyAreaModel.readCompanyByUserId(request.session.get("userid").get.toInt).get("company.name").get
      Ok(views.html.company.newJob(views.html.company.companyHeader("Create New Job",2),GeneralFunctions.someExtractor(companyName))) 
    }
    else{
      Logger.error("Invalid Session, loading failed from company newJob ")
      Redirect(routes.Application.index).flashing(
              "error" -> "You Dont have permission to load this page, try again later")
    }
  } 
  
  
  def saveNewJob = Action { implicit request =>
    try{
      if(checkSession(request)){ 
        /*
         * save newJob, newSkill, newLocation etc is defined only in adminAreaModel
         * In companyArea , we used to call the function in AdminAreaModel
         * */
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
                  Redirect(routes.CompanyArea.newJob).flashing(
                      "error" -> "Error Occured While Updating locations, But Partially updated")
                }
              }
              else{
                Redirect(routes.CompanyArea.newJob).flashing(
                    "error" -> "Error Occured While Updating skills")
              }
              Redirect(routes.CompanyArea.newJob).flashing(
                  "success" -> "job Created Successfully")
        }
        else{        
          Redirect(routes.CompanyArea.newJob).flashing(
              "error" -> "Job Creation Failed")
        }
      }
      else{
        Logger.error("Invalid Session, loading failed from company saveNewJob ")
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
  
  def jobList = Action { implicit request =>
    if(checkSession(request)){      
      val companyname = CompanyAreaModel.readCompanyByUserId(request.session.get("userid").get.toInt).get("company.name").get
      val jobList = CompanyAreaModel.readJobListByCompany(GeneralFunctions.someExtractor(companyname).toLowerCase())
      Ok(views.html.company.jobList(views.html.company.companyHeader("Job List",2),jobList))
    }
    else{
      Logger.error("Invalid Session, loading failed from admin jobList ")
        Redirect(routes.Application.index).flashing(
                "error" -> "You Dont have permission to load this page, try again latera")
    }
  }
  
  def applicationList = Action { implicit request =>
    if(checkSession(request)){
      val companyname = CompanyAreaModel.readCompanyByUserId(request.session.get("userid").get.toInt).get("company.name").get
      val jobList = CompanyAreaModel.readJobListByCompany(GeneralFunctions.someExtractor(companyname).toLowerCase())
      Ok(views.html.company.applicationList(views.html.company.companyHeader("Applications Received",2),jobList))
    }
    else{
      Logger.error("Invalid Session, loading failed from companyArea applicationList ")
        Redirect(routes.Application.index).flashing(
                "error" -> "You Dont have permission to load this page, try again latera")
    }
  }
  
  def apiApplicationList(hashId : String) = Action { implicit request =>
    val applicants = CompanyAreaModel.readApplicantsByJobId(hashId)
    Ok(views.html.company.applicationListApi(applicants))
  }
  
  def candidateViewInDetail(hashId : String) = Action {implicit request =>
    if(checkSession(request)){
      val candidateData  =  UserAreaModel.readCandidateByUserId(hashId)
      val resumeAddress="resume/"+GeneralFunctions.someExtractor(candidateData.get("candidate.resume").get)
      Ok(views.html.candidate.candidateViewInDetail(views.html.company.companyHeader("Applied Candidate In detail",2),candidateData,resumeAddress,"Company"))  
    }
    else{
      Logger.error("Invalid Session, loading failed from companyArea applicationList ")
        Redirect(routes.Application.index).flashing(
                "error" -> "You Dont have permission to load this page, try again latera")
    }
  }
  
  def sendMessageToApplicants =  Action{implicit request =>
    if(checkSession(request)){
      val companyname = CompanyAreaModel.readCompanyByUserId(request.session.get("userid").get.toInt).get("company.name").get
      val jobList = CompanyAreaModel.readJobListByCompany(GeneralFunctions.someExtractor(companyname).toLowerCase())
      Ok(views.html.company.sendMessageToApplicants(views.html.company.companyHeader("Send Message To Applicants",2),jobList)) 
    }
    else{
      Logger.error("Invalid Session, loading failed from company sendMessageToApplicants ")
        Redirect(routes.Application.index).flashing(
                "error" -> "You Dont have permission to load this page, try again latera")
    }
  }
  
  def doSendMessageToApplicants = Action{ implicit request =>
    if(checkSession(request)){
      try{
        val jobid = request.body.asFormUrlEncoded.get.get("job").get.head
        val message = request.body.asFormUrlEncoded.get.get("message").get.head
        val result = CompanyAreaModel.saveNewMessage(jobid, message)
        if(result>0){
          Redirect(routes.CompanyArea.sendMessageToApplicants).flashing(
                  "success" -> "Message Send Successfully")
        }
        else{
          Redirect(routes.CompanyArea.sendMessageToApplicants).flashing(
                  "error" -> "Message Sending failed")
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
    else{
      Logger.error("Invalid Session, loading failed from company sendMessageToApplicants ")
        Redirect(routes.Application.index).flashing(
                "error" -> "You Dont have permission to load this page, try again latera")
    }    
  }
  
  def jobViewInDetail(hashId :  String) = Action { implicit request =>
    if(checkSession(request)){      
      val jobData = AdminAreaModel.readSingleJob(hashId)
      val jobLocation = GeneralFunctions.getLocationStringFromMap(jobData)
      val jobSkill = GeneralFunctions.getSkillStringFromMap(jobData)
      Ok(views.html.company.jobViewInDetail(views.html.company.companyHeader("Job View in Detail",2),jobData.head,jobLocation,jobSkill))
    }
    else{
      Logger.error("Invalid Session, loading failed from company jobViewInDetail ")
        Redirect(routes.Application.index).flashing(
                "error" -> "You Dont have permission to load this page, try again latera")
    }
  }
}