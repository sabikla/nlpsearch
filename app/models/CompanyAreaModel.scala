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
object CompanyAreaModel {
  def readCompanyByUserId(companyId : Int) : Map[String,Any] = DB.withConnection{ implicit c =>
    val sql = SQL("SELECT * FROM company WHERE userid={companyid}").on(
      'companyid -> companyId  
    )          
    val result = sql().map(row =>      
      row.asMap
    ).toList
    result.head; 
  } 
  
  def readJobListByCompany(companyname : String) : List[Map[String,Any]] = DB.withConnection {implicit c => 
    val sql = SQL("""SELECT * FROM jobtable WHERE company={companyname}""").on(
      'companyname   -> companyname
    )
    val result = sql().map(row =>      
      row.asMap
    ).toList
    return result; 
  }
  
  def readApplicantsByJobId(hashId : String) : List[Map[String,Any]] = DB.withConnection {implicit c => 
    val sql = SQL("""select A.applicationid,A.userid,A.jobid,B.name from jobapplication A
INNER JOIN candidate B on A.userid=B.userid WHERE jobid={jobid}""").on(
      'jobid   -> hashId
    )
    val result = sql().map(row =>      
      row.asMap
    ).toList
    return result; 
  }
  
  
  def saveNewMessage(jobid : String, message : String ) : Int  = DB.withConnection { implicit c =>
    try{
      val result = SQL("INSERT INTO messages (jobid,message) VALUES({jobid},{message})").on(
          'jobid -> jobid,
          'message  -> message
          ).executeUpdate()  
      result
    }
    catch{
      case ex : PSQLException => {
        Logger.debug("saveNewMessage in CompanyAreaModel : "+ex.getMessage)
        return 0
      }
    }
  }
  
}