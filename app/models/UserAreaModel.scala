package models


import anorm._
import play.api.db._
import anorm.SqlParser._
import play.api.Play.current
import org.postgresql.util.PSQLException
import play.Logger

/**
 * @author qburst
 */
object UserAreaModel {
  
  def saveJobApplication(userId : Int , jobId : String) : Int  = DB.withConnection { implicit c =>
//    println(userId,jobId)
    try{
      val result = SQL("INSERT INTO jobapplication (userid, jobid) VALUES({userid},{jobid})").on(
          'userid -> userId,
          'jobid  -> jobId
          ).executeUpdate()
          result   
    }
    catch{
      case ex : PSQLException => {
        Logger.debug("saveJobApplication in UserAreaModel : "+ex.getMessage)
        return 0
      }
    }
  }   
  
  def readJobsApplied(userid : Int) :  List[Map[String,Any]]= DB.withConnection { implicit c =>
    val sql = SQL("""SELECT A.applicationid,A.userid,A.jobid,B.post,B.company FROM jobapplication A 
      INNER JOIN jobtable B on A.jobid=md5(CAST((B.jobid) AS text)) WHERE userid={userid}""").on(
      'userid -> userid  
    )          
    val result = sql().map(row =>      
      row.asMap
    ).toList
    return result; 
  }
  
  
  def readCandidateByUserId(hashId : String ) : Map[String,Any] = DB.withConnection { implicit c =>
    try{
      val sql = SQL("""SELECT * FROM candidate WHERE md5(CAST((userid) AS text))={hashId}""").on(
            'hashId ->  hashId
            )
      val result = sql().map(row =>      
        row.asMap
      ).toList
      result.head;
    } 
    catch{
      case ex : Exception => {
        Logger.debug("saveJobApplication in UserAreaModel : "+ex.getMessage)
        Map()
      }
    }
  }
  
  def readMessageByUserId(userId : Int) : List[Map[String,Any]] = DB.withConnection { implicit c =>
    try{
      val sql = SQL("""SELECT A.messageid,A.message,A.jobid FROM messages A JOIN jobapplication B on A.jobid=B.jobid                     
           WHERE userid={userId}""").on(
            'userId ->  userId
            )
      val result = sql().map(row =>      
        row.asMap
      ).toList
      result;
    }
    catch{
      case ex : Exception => {
        Logger.debug("saveJobApplication in UserAreaModel : "+ex.getMessage)
        List(Map())
      }
    }
    
  }
  
}