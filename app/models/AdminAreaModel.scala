package models

import play.api.db._
import anorm._
import anorm.SqlParser._
import play.api.Play.current
import org.postgresql.util.PSQLException
import play.Logger

/**
 * @author qburst
 */
object AdminAreaModel {
  def saveNewCompany(companyName : String, companyAddress : String) : Int  = DB.withConnection { implicit c =>
    try{
    	val result = SQL("INSERT INTO company (companyname, companyaddress) VALUES({companyName},{companyAddress})").on(
    			'companyName -> companyName,
    			'companyAddress -> companyAddress
    			).executeUpdate()
    			result   
    }
    catch{
      case ex : PSQLException => {
        Logger.debug("saveNewCompany in AdminModel : "+ex.getMessage)
        return 0
      }
    }
  }
  
  def readCompanyList : List[Map[String,Any]] = DB.withConnection {implicit c => 
    val sql = SQL("SELECT * FROM company")
    val result = sql().map(row =>      
      row.asMap
    ).toList
    return result; 
  }
  
  def readJobList : List[Map[String,Any]] = DB.withConnection {implicit c => 
    val sql = SQL("""SELECT * FROM jobtable""")
    val result = sql().map(row =>      
      row.asMap
    ).toList
    return result; 
  }
  
  def readFeedbackList : List[Map[String,Any]] = DB.withConnection {implicit c => 
    val sql = SQL("""SELECT * FROM feedback""")
    val result = sql().map(row =>      
      row.asMap
    ).toList
    return result; 
  }
  
  def readSingleJob(hashId : String) : List[Map[String,Any]] = DB.withConnection {implicit c => 
    val sql = SQL("""SELECT A.jobid,A.post,A.company,A.description,A.salary,A.experience,L.location,S.skill FROM jobtable A 
          INNER JOIN joblocation L on L.jobid=A.jobid
          INNER JOIN jobskill S on S.jobid=A.jobid
          WHERE md5(CAST((A.jobid) AS text))={jobid}""").on(
          'jobid ->  hashId
          )
    val result = sql().map(row =>      
      row.asMap
    ).toList
    return result; 
  }
  
  def readSingleFeedBack(hashId : String ) : Map[String,Any] = DB.withConnection { implicit c =>
    val sql = SQL("""SELECT * FROM feedback WHERE md5(CAST((feedbackid) AS text))={feedbackid}""").on(
          'feedbackid ->  hashId
          )
    val result = sql().map(row =>      
      row.asMap
    ).toList
    result.head; 
  }
  
  def saveNewJob(job : String, company : String, description : String, salary : Float, experience: Int ) : Int  = DB.withConnection { implicit c =>
    try{
      var jobid : Int =0
      val result = SQL("INSERT INTO jobtable (post,company,description,salary,experience) VALUES({post},{company},{description},{salary},{experience})").on(
          'post -> job.toLowerCase(),
          'company -> company.toLowerCase(), 
          'description -> description,
          'salary -> salary,
          'experience -> experience
          ).executeUpdate()
      if(result > 0){
        val qry = SQL("SELECT max(jobid) as jobid FROM jobtable")
        qry().map { row =>          
          jobid =(row.asMap.get(".jobid").get).toString().toInt
        }
      }  
      jobid
    }
    catch{
      case ex : PSQLException => {
        Logger.debug("saveNewJob in AdminModel : "+ex.getMessage)
        return 0
      }
    }
  }
  
  
  
  def saveJobLocation(jobid : Int, location : String ) : Int  = DB.withConnection { implicit c =>
    try{
      val result = SQL("INSERT INTO joblocation (location,jobid) VALUES({location},{jobid})").on(
          'location -> location.trim().toLowerCase(),
          'jobid -> jobid
          ).executeUpdate()  
      result
    }
    catch{
      case ex : PSQLException => {
        Logger.debug("saveJobLocation in AdminModel : "+ex.getMessage)
        return 0
      }
    }
  }
  
  
  
  def saveJobSkill(jobid : Int, skill : String ) : Int  = DB.withConnection { implicit c =>
    try{
      val result = SQL("INSERT INTO jobskill (skill,jobid) VALUES({skill},{jobid})").on(
          'skill -> skill.trim().toLowerCase(),
          'jobid -> jobid
          ).executeUpdate()  
      result
    }
    catch{
      case ex : PSQLException => {
        Logger.debug("saveJobSkill in AdminModel : "+ex.getMessage)
        return 0
      }
    }
  }
}