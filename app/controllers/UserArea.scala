package controllers

import play.api._
import play.api.mvc._
import models.ApplicationModel
import models.UserAreaModel


/**
 * @author qburst
 */
class UserArea extends Controller {
  
  def checkSession (request : Request[AnyContent]) : Boolean = {
    request.session.get("userid").map { user =>
      val userType = request.session.get("usertype").get
      if(userType.toInt==2){        
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
      if(!request.cookies.get("jobToApply").isEmpty){
          Ok(views.html.candidate.candidateDashboard(views.html.candidate.candidateHeader("Dashboard",1),1))
//        Ok(views.html.candidate.candidateHeader("Dashboard",1)).discardingCookies(DiscardingCookie("jobToApply"))
      } 
      else{        
    	  Ok(views.html.candidate.candidateHeader("Dashboard",1))
      }
    }
    else{
      Logger.error("Invalid Session, loading failed from candidate dashboard ")
        Redirect(routes.Application.index).flashing(
                "error" -> "You Dont have permission to load this page, try again later")
    }
  }
  
  def searchForJob = Action {implicit request =>
    if(checkSession(request)){
//     Ok(views.html.searchResult(views.html.candidate.candidateHeader("Search For Job",2)))
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
      Ok(views.html.candidate.searchResult(views.html.candidate.candidateHeader("Job Search Result",2),resultSet, locationMap, skillMap))
    }
    else{
      Logger.error("Invalid Session, loading failed from candidate dashboard ")
        Redirect(routes.Application.index).flashing(
                "error" -> "You Dont have permission to load this page, try again later")
    }
  }
  
  def confirmJobApplication = Action { implicit request =>
    if(checkSession(request)){      
      val jobId = request.cookies.get("jobToApply").get.value.toString()
      val userId = request.session.get("userid").get.toInt
      val result = UserAreaModel.saveJobApplication(userId, jobId)
      if(result>0){        
    	  Redirect(routes.UserArea.dashboard).flashing(
                "success" -> "You Application submited successfully").discardingCookies(DiscardingCookie("jobToApply"))
      }
      else{
        Redirect(routes.UserArea.dashboard).flashing(
                "error" -> "Failed to apply for this job, Please try again.").discardingCookies(DiscardingCookie("jobToApply"))
      }
      
    }
    else{
      Logger.error("Invalid Session, loading failed from candidate searchForJob ")
        Redirect(routes.Application.index).flashing(
                "error" -> "You Dont have permission to load this page, try again later")
    }
  }
  
  def declaneJobApplication = Action {implicit request =>
    if(checkSession(request)){
       Redirect(routes.UserArea.dashboard).flashing(
                "success" -> "You Application discarded successfully").discardingCookies(DiscardingCookie("jobToApply"))
    }
    else{
      Logger.error("Invalid Session, loading failed from candidate declaneJobApplication ")
        Redirect(routes.Application.index).flashing(
                "error" -> "You Dont have permission to load this page, try again later")
    }
  }
  
  def myApplications = Action { implicit request =>
    if(checkSession(request)){
      val jobsApplied = UserAreaModel.readJobsApplied(request.session.get("userid").get.toInt)
      Ok(views.html.candidate.myApplications(views.html.candidate.candidateHeader("My Applications",2),jobsApplied))
    }
    else{
      Logger.error("Invalid Session, loading failed from candidate myApplication")
        Redirect(routes.Application.index).flashing(
                "error" -> "You Dont have permission to load this page, try again later")
    }
  }
  
  def messageList = Action { implicit request =>    
    if(checkSession(request)){
      val userid =request.session.get("userid").get.toInt
      val messages = UserAreaModel.readMessageByUserId(userid)
      println(messages)
      Ok(views.html.candidate.messageList(views.html.candidate.candidateHeader("Message list",3),messages))
    }
    else{
      Logger.error("Invalid Session, loading failed from candidate messageList")
        Redirect(routes.Application.index).flashing(
                "error" -> "You Dont have permission to load this page, try again later")
    }
  }
}