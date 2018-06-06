package com.pingpong.app;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;


import uk.co.smartkey.jforumsecuresso.SecurityTools;







public class Myservlet extends HttpServlet {
  public boolean test = true;
  public String emailError = "No";
  public String exceptionMsg="";
  private HttpSession session;
  public static int pwdValid=0;
  private static Map<String, String> map= new HashMap<String, String>(100);

  
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);
  }
  
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
	session = request.getSession();
	
	  
    pwdValid=0;
    try {
     
      String command = (String)request.getParameter(ConfigConstants.COMMAND_KEY);
      ConfigLogUtil.log(ConfigConstants.LOGFILE, command);
      if (command == null || command.trim().length() == 0)
        throw new ConfigException ("Invalid request command.", true);
        
    
      //test a connection first because of a mysql bug.
      Connection con = getConnectTionFromPool();
	  String teststmt = "select * from login";
	  ConfigLogUtil.log(ConfigConstants.LOGFILE, "calling from doGet for testing purpose.");
	  executeQueryStmt(con, teststmt);
	  
	  try {
		  Connection con1;
		  while (!test){
			  con1 = getConnectTionFromPool();  
			  ConfigLogUtil.log(ConfigConstants.LOGFILE, "calling from doGet.");
			  executeQueryStmt(con1, teststmt);
			  try {
				  con1.close();
			  }catch (Exception m){
				  ConfigLogUtil.log(ConfigConstants.LOGFILE, m.getMessage()); 
			  }
		  
		  }
		  con.close();
		  
	  }catch(Exception n){
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, n.getMessage());
		  
	  }
	  
	  // test done
	  //from ajax
	  if(command.equals(ConfigConstants.COMMAND_SINGLEREPORT)) 
		doSingleReport(request, response);
	  if(command.equals(ConfigConstants.COMMAND_EVENTS)) 
		  doEvent(request, response);
	  if(command.equals(ConfigConstants.COMMAND_FINDTEAMS)) 
		  doTeam(request, response);
	  //done
	  if(command.equals(ConfigConstants.COMMAND_FINDGAMES)) 
		  doGame(request, response);
      if (command.equals(ConfigConstants.COMMAND_LOGIN))
        doLogin(request, response);
      if (command.equals(ConfigConstants.COMMAND_BACKLOGIN))
          doBackLogin(request, response);
      if (command.equals(ConfigConstants.COMMAND_MEMBERINFO))
    	 doMemberInfo(request, response);
      if (command.equals(ConfigConstants.COMMAND_REQINFO))
     	 doSelectMember(request, response);
      if (command.equals(ConfigConstants.COMMAND_RESULTS))
    	  doSendEmail(request, response);
      if (command.equals(ConfigConstants.COMMAND_STATUS))
    	  doStatus(request, response);
      if (command.equals(ConfigConstants.COMMAND_UPDATESTATUS))
    	  doStatusUpdate(request, response);
      if (command.equals(ConfigConstants.COMMAND_TASKS))
    	  doSelectTask(request, response);
      if (command.equals(ConfigConstants.COMMAND_UPDATE))
    	  doMemberUpdate(request, response);
      if (command.equals(ConfigConstants.COMMAND_FINDPWD))
    	  doFindPwd(request, response);
      if (command.equals(ConfigConstants.COMMAND_SCHEDULE))
    	  doSchedule(request, response);
      if (command.equals(ConfigConstants.COMMAND_SEARCH))
    	  doSearchSchedule(request, response);
      if (command.equals(ConfigConstants.COMMAND_SEARCHCOACH))
    	  doSelectCoach(request, response);
      if (command.equals(ConfigConstants.COMMAND_GAMECONFIG))
    	  doGameConfig(request, response);
      if (command.equals(ConfigConstants.COMMAND_ROUNDROBIN))
    	  doRoundRobin(request, response);
      if (command.equals(ConfigConstants.COMMAND_COACH))
    	  doContactCoach(request, response);
      if (command.equals(ConfigConstants.COMMAND_PLAYOFF))
    	  doPlayOff(request, response);
      if (command.equals(ConfigConstants.COMMAND_GAMEEDIT))
    	  doGameEdit(request, response);
      if (command.equals(ConfigConstants.COMMAND_ROUNDROBINEDIT))
    	  doGameEdit(request, response);
      if (command.equals(ConfigConstants.COMMAND_PLAYOFFEDIT))
    	  doGameEdit(request, response);
      if (command.equals(ConfigConstants.COMMAND_GAMESHOW))
    	  doGameShow(request, response);
      if (command.equals(ConfigConstants.COMMAND_ROUNDROBINSHOW))
    	  doGameShow(request, response);
      if (command.equals(ConfigConstants.COMMAND_PLAYOFFSHOW))
    	  doGameShow(request, response);
      if (command.equals(ConfigConstants.COMMAND_CREATELEAGUE))
    	  doCreateLeague(request, response);
      if (command.equals(ConfigConstants.COMMAND_JOINLEAGUE))
    	  doJoinLeague(request, response);
      if (command.equals(ConfigConstants.COMMAND_JOINTOURNAMENT))
    	  doJoinTournament(request, response);
      if (command.equals(ConfigConstants.COMMAND_MANAGELEAGUE))
    	  doManageLeague(request, response);
      if (command.equals(ConfigConstants.COMMAND_EDITLEAGUE))
    	  doEditLeague(request, response);
        
    } catch (ConfigException ce)
    {
    	ConfigLogUtil.log(ConfigConstants.LOGFILE, ce.getMessage());
      ce.printStackTrace();
      ErrorBean errorBean = new ErrorBean();
      errorBean.seterrormsg(ce.getMessage());
      errorBean.setrequirelogin(ce.getRequireLogin());
      if (session != null)
        session.setAttribute("errorBean", errorBean);
      request.getRequestDispatcher("error.jsp").forward(request, response);     
    } catch (Exception e)
    {
    	ConfigLogUtil.log(ConfigConstants.LOGFILE, e.getMessage());
      e.printStackTrace();
      ErrorBean errorBean = new ErrorBean();
      errorBean.seterrormsg("Internal server error.");
      errorBean.setdetailmsg(e.toString());
      errorBean.setrequirelogin(true);
      if (session != null)
        session.setAttribute("errorBean", errorBean);
      request.getRequestDispatcher("error.jsp").forward(request, response);     
    }
  }
  private void doSingleReport(HttpServletRequest request, HttpServletResponse response){
	  
	  try{
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, "get into doSingleReport");
		  String task = (String) request.getParameter("submit");
		  Connection rcon = getConnectTionFromPool();
		  String st = "select * from temprating";
		  ResultSet rs = executeQueryStmt(rcon, st);
		  
		  if (task.contains("Update")){
			  while(rs.next()){
				  String name=rs.getString(3);
				  int rate = rs.getInt(5);
				  String update= "update leaguemembers set InitRate='"+rate+"' where LeagueName ='"+(String)session.getAttribute("leaguename")+"' and MemName='"+name+"' and MemEmail in (select Email from indivitournament where MatchName='"+(String)request.getParameter("gamename")+"')";
				  ConfigLogUtil.log(ConfigConstants.LOGFILE, update);
				  executeStmt(rcon, update);
				  String drop ="Delete from temprating where LeagueName='"+(String)session.getAttribute("leaguename")+"'";
				  executeStmt(rcon, drop);
			  }
		  }else{
			  String cp = "update leaguemembers set PreTournamentRate = InitRate where LeagueName='"+(String)session.getAttribute("leaguename")+"'";
			  ConfigLogUtil.log(ConfigConstants.LOGFILE, cp);
			  executeStmt(rcon, cp);
			  
		  }
		    
		  //put delete temprating here.
		  rcon.close();
		  //if update do copy NowRate
		  //if save do copy initRate to PreTournament
		  String message = "The rates are updated";
		  request.setAttribute("ConfirmMessage", message);
		  request.getRequestDispatcher("confirm.jsp").forward(request, response);
		  
		  
	  }catch(Exception n){
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, n.getMessage());
		  
	  }
	  
	  
	  
	  
	  
  }
  private void doSearchSchedule(HttpServletRequest request, HttpServletResponse response){
	  try{
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, "get into doSearchSchedule");
		  String back = (String)request.getParameter("back");
		  if (back!=null && back.equals("back")){
			  request.getRequestDispatcher("tasks.jsp").forward(request, response);
			  return;
		  }
		 
		 
		  String expectLow = (String) request.getParameter("expectlow");
		  String expectHigh = (String) request.getParameter("expecthigh");
		  if (expectLow.equals("none"))
			  expectLow="0";
		  
		  if (expectHigh.equals("none"))
			  expectHigh="2601";
		  
		
		 
		  String Email = (String)session.getAttribute("sessionMail");
		
		  //String Location="";
		//  String[] raterange = expectRate.split("-");
		  String lowrate = expectLow;
		  String highrate = expectHigh;
		  String lastname = (String)request.getParameter("lastname");
		  if (lastname  == null)
			  lastname = "";
		  lastname.toLowerCase();
		
		  
		  String state = (String)request.getParameter("state");
		  if (state==null)
			  state="";
		  state.toUpperCase();
		  
		  String country = (String)request.getParameter("country");
		  if (country==null)
			  country="";
		  country.toUpperCase();
		  
		  
		  //" and schedules.club like "+"'%"+Location+"%'"
		  String Location=" and schedules.club in (";
		  String[] Locations = (String[])request.getParameterValues("club");
		  for(int i=0; i<Locations.length; i++){
			  if (i > 0)
				  Location = Location + ",";
			  Location=Location+"'"+Locations[i]+"'";
			  
		  }
		  Location = Location + ")";
		  
		  String stmt2 = "select Username, Highrate, club, date, day, time, frequency from ppmembers, schedules where ppmembers.Email=schedules.email and ppmembers.Lowrate >"+"'"+lowrate+"'"+" and ppmembers.Highrate <"+"'"+highrate+"'"+" and ppmembers.Username like '%"+lastname+"'"+ Location +" and schedules.state="+"'"+state+"'";
		  
			ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt2);
			Connection con = getConnectTionFromPool();
			ResultSet rs2 = executeQueryStmt(con, stmt2);
			//con.close();
			request.setAttribute("tempcon", con);
			
			request.setAttribute("schedule", rs2);
			session.setAttribute("sessionClub", Location);
			request.getRequestDispatcher("scheduletable.jsp").forward(request, response);
	  }catch(Exception n){
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, n.getMessage());
		  
	  }
		
		 
	  
	  
	  
  }
  
  private void doEditLeague(HttpServletRequest request, HttpServletResponse response){
	  
	  try{
		  String leaguename = (String)request.getParameter("leaguename");
		  String name = (String)request.getParameter("name");
		  String rate = (String)request.getParameter("initrate");
		  String mail = (String)request.getParameter("memmail");
		  String oldname = (String)request.getParameter("oldname");
		  Connection con = getConnectTionFromPool();
		  String st1 = "delete from leaguemembers where MemName='"+oldname+"'";
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, st1);
		  String st2 = "Insert into leaguemembers values ('"+leaguename+"', '"+name+"', '"+rate+"', '"+mail+"', "+rate+")";
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, st2);
		  executeStmt(con, st1);
		  executeStmt(con, st2);
		  String message = "The member info is updated";
		  request.setAttribute("ConfirmMessage", message);
		  request.getRequestDispatcher("confirm.jsp").forward(request, response);
		  
		  
	  }catch(Exception e){
		  
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, e.getMessage());
		  
	  }
	  
	  
  }
  
  private void doManageLeague(HttpServletRequest request, HttpServletResponse response){
	  
	  try{
		  Connection con = getConnectTionFromPool();
		  String view = (String)request.getParameter("view");
		  if (view != null && view.equals("coach")){
			  String st1 = "select Username, SelfRate, Level, City, State from ppmembers where Country='USA' and Coach='Yes' order by State";
			  ResultSet coaches=executeQueryStmt(con, st1);
			  request.setAttribute("con", con);
			  request.setAttribute("coaches", coaches);
			  ConfigLogUtil.log(ConfigConstants.LOGFILE, "return viewcoach");
			  request.getRequestDispatcher("viewcoach.jsp").forward(request, response);
			 
			  return;
		  }
		  
		  if (view != null && view.equals("lover")){
			  String st1 = "select Username, SelfRate, City, State from ppmembers where Country='USA' order by State";
			  ResultSet lovers=executeQueryStmt(con, st1);
			  request.setAttribute("con", con);
			  request.setAttribute("lovers", lovers);
			  ConfigLogUtil.log(ConfigConstants.LOGFILE, "return viewlover");
			  request.getRequestDispatcher("viewlover.jsp").forward(request, response);
			 
			  return;
		  }
		  
		  String leaguename = (String)session.getAttribute("leaguename");
		  String admin = (String)request.getParameter("admin");
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, admin);
		  
		  if (admin.equals("search")){
			  String memname = (String) request.getParameter("memname");
			  if (memname==null || memname.equals("")){
				  memname="%";
			  }
			  //String leaguename = (String) request.getParameter("leaguename");
			  String stmt = "select * from leaguemembers where LeagueName='"+leaguename+"' and MemName like '%"+memname+"%'";
			  ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt);
			  ResultSet rs=executeQueryStmt(con, stmt);
			  session.setAttribute("rs1", rs);
			  session.setAttribute("temcon", con);
			  request.getRequestDispatcher("listmembers.jsp").forward(request, response);
			  return;
		  }
		  String ac="";
		  if(admin.equals("tournament")){
			  String  action = (String) request.getParameter("TournamentName");
			  ConfigLogUtil.log(ConfigConstants.LOGFILE, action);
			  String  name1 = (String) request.getParameter("tournamentname");
			  												
			  
			 // String name = (String)request.getParameter("tournamentname");
			  if (action.equals("Delete")){
				  if (name1 != null && (! name1.equals("")) && (! name1.equals("no"))){
				  String stmt1 = "delete from leaguetournament where Tournament='"+name1+"'";
				  ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt1);
				  executeStmt(con, stmt1);
				  String stmt2 = "delete from leagueteam where Tournament='"+name1+"'";
				  ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt2);
				  executeStmt(con, stmt2);
				  ac = "deleted";
				  }
			  }
			  
			  if (action.equals("Create")||action.equals("Edit"))
			  {
				  String  name2 = (String) request.getParameter("newtmname");
				  if (name2 != null &&(!name2.equals("")) && (!name2.equals("no"))){
				  String stmt1 = "insert into leaguetournament values ('"+leaguename+"', "+"'"+name2+"')";
				  ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt1);
				  if(!executeStmt(con, stmt1)){
					  String message11 = "The tournament name has been used.";
					  request.setAttribute("ConfirmMessage", message11);
					  request.getRequestDispatcher("confirm.jsp").forward(request, response);
					  con.close();
					  return;
				  }
				  
				  
				  ac = "created";
				  }else{
					  name2=(String) request.getParameter("tournament");
				  }
				  con.close();
				  request.setAttribute("leaguename", leaguename);
				  request.setAttribute("tournamentname", name2);
				  request.getRequestDispatcher("events.jsp").forward(request, response);
				  
				  return;
			  }
			 
			  
		  }
		  if (admin.equals("report")){
			  String  name1 = (String) request.getParameter("tournamentname");
			  String  game = (String) request.getParameter("gamename");
			  if (game == null)
				  game="";
			  session.setAttribute("tn", name1);
			  //check if it is Team league or single league.
			  String tstm0 = "select LeagueType from leagues, leaguetournament where leaguetournament.LeagueName=leagues.LeagueName and leaguetournament.Tournament='"+name1+"'";
			  ConfigLogUtil.log(ConfigConstants.LOGFILE, tstm0);
			  ResultSet type = executeQueryStmt(con, tstm0);
			  String ltype="";
			  while(type.next()){
				  ltype=type.getString(1);
			  }
			  if (!ltype.equals("Team"))
				  ltype="none";
			  
			  if(ltype.equals("none")){
				  
				  //String tstm1="select distinct Username, Highrate from ppmembers, indivitournament where ppmembers.Email=indivitournament.Email and indivitournament.Tournament='"+name1+"'  order by Highrate";
				  //Checking single report type here
				  String  detail = (String)request.getParameter("reportdetail");
				  ConfigLogUtil.log(ConfigConstants.LOGFILE, "I am still alive here.");
				  String tstm1="";
				 
				  if (detail != null && detail.contains("Rating")){
					  
					  tstm1="select LeagueName, MemName, PreTournamentRate, InitRate from leaguemembers where leaguemembers.LeagueName='"+leaguename+"'";
				  }else{
					  tstm1="select distinct MemName, InitRate, MatchName, regtime from leaguemembers, indivitournament where leaguemembers.LeagueName='"+leaguename+"' and indivitournament.MatchName='"+game+"'"+ " and leaguemembers.MemEmail=indivitournament.Email and indivitournament.Tournament='"+name1+"'  order by regtime";
				  }
				 // ConfigLogUtil.log(ConfigConstants.LOGFILE, tstm1);
				  
				  
				  
				  //end
				  
				  ConfigLogUtil.log(ConfigConstants.LOGFILE, tstm1);
				  ResultSet singlereport = executeQueryStmt(con, tstm1);
				  //request.setAttribute("detail", detail);
				  request.setAttribute("singlereport", singlereport);
				  request.setAttribute("con", con);
				  if (detail != null && detail.contains("Rating")){
					  request.getRequestDispatcher("singlereport.jsp").forward(request, response);
				  }else{
					  request.getRequestDispatcher("individualreport.jsp").forward(request, response);
				  }
				  return;
				  
			  }
			  
			  
			  
			  
			  if (name1 != null && (! name1.equals(""))&& (! name1.equals("no"))){
				  String stmt3 = "select distinct teammembers.TeamName, MemName, MatchName from teammembers, leagueteam where leagueteam.Tournament='"+name1+"' and leagueteam.MatchName='"+game+"'"+" and leagueteam.TeamName=teammembers.TeamName order by TeamName";
				  ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt3);
				  ResultSet rp = executeQueryStmt(con, stmt3);
				  request.setAttribute("match", game);
				  request.setAttribute("teamreport", rp);
				  request.setAttribute("con", con);
				  request.getRequestDispatcher("teamreport.jsp").forward(request, response);
				  return;
				  
				  
				  
			  }
			  
			  
			  
		  }
		  /*
		  if (admin.equals("team")){
			  String tnname = (String)request.getParameter("tournamentname");
			  String action = (String) request.getParameter("TeamName");
			  String name = (String)request.getParameter("teamname");
			  //add tournament.
			  if (action.equals("Delete")){
				  String stmt1 = "delete from leagueteam where TeamName='"+name+"'";
				  ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt1);
				  executeStmt(con, stmt1);
				  String stmt2 = "delete from teammembers where TeamName='"+name+"'";
				  ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt2);
				  executeStmt(con, stmt2);
				  ac="deleted";
			  }else{
				  String stmt1 = "insert into leagueteam values ('"+leaguename+"', "+"'"+name+"', '"+tnname+"')";
				  ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt1);
				  executeStmt(con, stmt1);
				  ac = "created";
			  }
			  
		  }*/
		  
		  String message = "The "+ admin + " is "+ac+"!";
		  request.setAttribute("ConfirmMessage", message);
		  request.getRequestDispatcher("confirm.jsp").forward(request, response);
		  
	  }catch(Exception e){
		  
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, e.getMessage());
		  
	  }
	  
	  
  }
  private void doCreateLeague(HttpServletRequest request, HttpServletResponse response){
	  
	  try {
		  	String lname = (String) request.getParameter("leaguename");
		  	String ltype = (String) request.getParameter("leaguetype");
		  	String tournamentname = (String) request.getParameter("tournamentname");
		  	if (tournamentname==null || tournamentname.equals(""))
		  		tournamentname="notfor"+lname;
		  	String dirMail = (String)session.getAttribute("sessionMail"); 
		  	String stmt = "insert into leagues values ("+"'"+lname+"', "+"'"+ltype+"', "+
		  			  "'"+dirMail+"')";
		  	ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt);
		  	Connection con = getConnectTionFromPool();
			
			if(!executeStmt(con, stmt)){
				con.close();
				String message = "one person can only create one league.";
				request.setAttribute("ConfirmMessage", message);
				request.getRequestDispatcher("confirm.jsp").forward(request, response);
				return;
			}
			String stmt1="";
			if (tournamentname.equals("no")){
				stmt1 = "insert into leaguetournament values ("+"'"+lname+"', "+"'"+tournamentname+"')";
			
				ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt1);
				executeStmt(con, stmt1);
			}
			session.setAttribute("leaguename", lname);
			con.close();
			String message = lname + " league is created and you are the director of this league";
			request.setAttribute("ConfirmMessage", message);
			request.getRequestDispatcher("confirm.jsp").forward(request, response);
			
		  
	  }catch(Exception n){
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, n.getMessage());
		  
	  }
	  
	  
	  
	  
  }
  
  
private void doEvent(HttpServletRequest request, HttpServletResponse response){
	try{
		Connection con = getConnectTionFromPool();
		String tname = (String) request.getParameter("tname");
		for (int i=1; i<25; i++){
			String event = (String) request.getParameter("event"+i);
			
			if(event != null && !event.equals("")){
				event = tname+"-"+event;
				String stmt = "insert into games values ('"+event+"', "+"'"+null+"', "+null+", "+null+", "+"'"+tname+"', "+null+", '"+(String)session.getAttribute("sessionMail")+"', "+"'N')";
				executeStmt(con, stmt);
			}
		}
		con.close(); 
		String message = "All events have been created.";
		request.setAttribute("ConfirmMessage", message);
		request.getRequestDispatcher("confirm.jsp").forward(request, response);
	}catch(Exception e){
		ConfigLogUtil.log(ConfigConstants.LOGFILE, e.getMessage());
	}
	
}
  
private void doJoinLeague(HttpServletRequest request, HttpServletResponse response){
	  
	try {
	  	String lname = (String) request.getParameter("leaguename");
	  	String myrate = (String) request.getParameter("initrate");
	  	String getrates = "select Lowrate, Highrate from ppmembers where Email='"+(String)session.getAttribute("sessionMail")+"'";
	  	Connection con = getConnectTionFromPool();
	  	ResultSet lrates = executeQueryStmt(con, getrates);
	  	int totalrates=0;
	  	while(lrates.next()){
	  		totalrates=lrates.getInt(1)+lrates.getInt(2);
	  		
	  	}
	  	
	  	
	  	int lrate = totalrates/2; 
	  	if (myrate != null && !myrate.equals("")){
	  		
	  		lrate=Integer.parseInt(myrate);
	  	}
	  	
	  	String mname = (String)session.getAttribute("sessionName");
	  	//setup leaguename in ppmembers.
	  	String lstate = "select LeagueName from ppmembers where Email='"+(String)session.getAttribute("sessionMail")+"'";
	  	ResultSet lstatus = executeQueryStmt(con, lstate);
	  	String myleague="";
	  	while(lstatus.next()){
	  		myleague=lstatus.getString(1);
	  		
	  	}
	  	
	  	if (myleague != null && !myleague.equals("")){
	  		myleague = myleague+":"+lname;
	  		String stmt1="update ppmembers set LeagueName='"+myleague+"' where Email='"+(String)session.getAttribute("sessionMail")+"'";
			executeStmt(con, stmt1);
			ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt1);
	  	}else{
	  		String stmt1="update ppmembers set LeagueName='"+lname+"' where Email='"+(String)session.getAttribute("sessionMail")+"'";
			executeStmt(con, stmt1);
			ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt1);
	  		
	  	}
	  	
	  	
//done.
	  		  	
	  	String stmt = "insert into leaguemembers values ("+"'"+lname+"', "+"'"+mname+"', "+
	  			  lrate + ", '"+(String)session.getAttribute("sessionMail")+"',"+ " 0)";
		executeStmt(con, stmt);
		ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt);
		con.close();
		String message = "You have joined "+lname+" league and the rate will be verified if you have.";
		request.setAttribute("ConfirmMessage", message);
		request.getRequestDispatcher("confirm.jsp").forward(request, response);
		
	  
  }catch(Exception n){
	  ConfigLogUtil.log(ConfigConstants.LOGFILE, n.getMessage());
	  
  } 
	
	
}
	  
	private void doJoinTournament(HttpServletRequest request, HttpServletResponse response){
		  
		try {
			ConfigLogUtil.log(ConfigConstants.LOGFILE, "I am in doJoinTournament");
		  	String tname = (String) request.getParameter("teamname");
		  	String newteam = (String) request.getParameter("newteam");
		  	String task = (String) request.getParameter("submit");
		  	
		  	if (newteam != null && !newteam.equals("")){
		  		newteam = newteam.toUpperCase();
		  	
		  		tname = newteam;
		  	}
		  	//String leaguename = (String)session.getAttribute("leaguename");
		  	
		  	String tournament = (String)request.getParameter("tournamentname");
		  	String gamename = (String)request.getParameter("gamename");
		  	String check = (String) request.getParameter("admin");
		  	String leader="";
		  	String stmt0 = "select LeagueName from leaguetournament where Tournament='"+ tournament +"'";
		  	Connection con = getConnectTionFromPool();
		  	ResultSet ln = executeQueryStmt(con, stmt0);
		  	ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt0);
		  	String lname="";
		  	while(ln.next()){
		  		lname=ln.getString(1);
		  		
		  	}
		  	
		  	String mname = (String)session.getAttribute("sessionName"); 
		  	if ((tname!=null) && !(tname.equals(""))){
		  		tname = tname.toUpperCase();
		  		ConfigLogUtil.log(ConfigConstants.LOGFILE, "I am here11");
		  		if (newteam!=null && !newteam.equals("")){
		  			ConfigLogUtil.log(ConfigConstants.LOGFILE, "I am here12");
		  			leader="yes";
		  			String stmt00="delete from leagueteam where MatchName='"+gamename+"' and TeamName in (select TeamName from teammembers where MemName='"+mname+"' and Tournament='"+tournament+"' and TeamLeader='yes')";
		  			ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt00);
		  			executeStmt(con, stmt00);
		  			String stmt01="delete from teammembers where TeamName not in (select TeamName from leagueteam)";
		  			
		  			ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt01);
		  			executeStmt(con, stmt01);
		  			
		  			String stmt1="insert into leagueteam values ("+"'"+lname+"', "+"'"+tname+"', "+
				  			  "'"+tournament+"', '"+gamename+"')";
		  			ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt1);
		  			//executeStmt(con, stmt1);
		  			if(!executeStmt(con, stmt1)){
		  				 con.close();
						  String message11 = "The team name has been used.";
						  request.setAttribute("ConfirmMessage", message11);
						  request.getRequestDispatcher("confirm.jsp").forward(request, response);
						  return;
					  }
		  			
		  		}
		  	
		  		// if  check=create
		  		// leader=yes and add team to leagueteams
		  		//else leader=no;
		  		
		  		
		  		String stmt = "insert into teammembers values ("+"'"+tname+"', "+"'"+mname+"', "+
		  			  "'"+leader+"'"+", '"+tournament+"'"+")";
		  		ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt);
		  		//add team
		  
			
		  		executeStmt(con, stmt);
		  	}else{
		  		
		  		ConfigLogUtil.log(ConfigConstants.LOGFILE, "I am here13");
		  		String email =(String)session.getAttribute("sessionMail");
		  		String stmt2="insert into indivitournament values ("+"'"+tournament+"', "+
			  			  "'"+mname+"', "+"'"+email+"'"+", '"+gamename+"', NOW())";
		  		String stmt3 = "delete from indivitournament where Email='"+email+"' and MatchName='"+gamename+"'";
		  		ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt2);
		  		//executeStmt(con, stmt2);
		  		if(!task.equals("Delete")){
		  			if(!executeStmt(con, stmt2)){
		  				con.close();
		  				String message11 = "The player name could not be added.";
		  				request.setAttribute("ConfirmMessage", message11);
		  				request.getRequestDispatcher("confirm.jsp").forward(request, response);
		  				return;
		  			}
		  		}else{
		  			if(!executeStmt(con, stmt3)){
		  				con.close();
		  				String message11 = "The name could not be removed from the list.";
		  				request.setAttribute("ConfirmMessage", message11);
		  				request.getRequestDispatcher("confirm.jsp").forward(request, response);
		  				return;
		  			}
		  		}
		  		
		  		//add individual name to tournament.
		  	}
		  	
		  	con.close();
		  	String message="";
		  	if(!task.equals("Delete")){
		  		message = "You have joined "+tournament+".";
		  	}else{
		  		message = "Your name has been removed.";
		  	}
		  	request.setAttribute("ConfirmMessage", message);
		  	request.getRequestDispatcher("confirm.jsp").forward(request, response);
			
		  
	  }catch(Exception n){
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, "I got exeception");
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, n.getMessage());
		  
	  }    
	  
	  
  }
  
  private void doMemberUpdate(HttpServletRequest request, HttpServletResponse response){
	  Vector rates=new Vector();
	  String[] temp;
	  
	  try{
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, "In the doMemberUpdate method.");
		  String back = (String)request.getParameter("back");
		  if (back!=null && back.equals("back")){
			  request.getRequestDispatcher("tasks.jsp").forward(request, response);
			  return;
		  }
		  //should be session mail
		  String Email = (String)session.getAttribute("sessionMail");
		  String email = (String)request.getParameter("email");
		  if (!Email.equals(email)){
			  request.setAttribute("mailError", "Yes");
			  request.getRequestDispatcher("memberinfo.jsp").forward(request, response);
			  return;
			  
		  }
		  request.setAttribute("mailError", "No");
		  Connection con = getConnectTionFromPool();
		  //Delete old profile.
		  String stmt1 = "delete from login where Email="+"'"+Email+"'";
		  String stmt2 = "delete from ppmembers where Email="+"'"+Email+"'";
		  executeStmt(con, stmt1);
		  executeStmt(con, stmt2);
		  
		  String lastName= (String)request.getParameter("lname");
		  String firstName= (String)request.getParameter("fname");
		  String teamname = (String)request.getParameter("teamname");
		  String coach = (String)request.getParameter("coach");
		  String level = (String)request.getParameter("certified");
		  String leaguename = (String)request.getParameter("leaguename");
		  if (leaguename == null)
			  leaguename="";
		  leaguename.toUpperCase();
		  if (teamname == null)
			  teamname="";
		  teamname.toUpperCase();
		  String state= (String)request.getParameter("state");
		  //state.toUpperCase();
		  String country = (String)request.getParameter("country");
		 // country.toUpperCase();
		  String city = (String)request.getParameter("city");
		  if (city == null)
			  city="";
		  city.toUpperCase();
		  String userName= firstName + " " + lastName;
		  userName = userName.toLowerCase();
		 
		  
		 
		  
		  String pwd = (String)request.getParameter("pwd");
		  String rpwd = (String)request.getParameter("rpwd");
		  String pwderror = "No";
		  if (!pwd.equals(rpwd)){
			  
			  request.setAttribute("pwdError", "Yes");
			  request.getRequestDispatcher("memberinfo.jsp").forward(request, response);
			  return;
		  }  
		  String leagueId = (String) request.getParameter("leaguerate");
		  if(leagueId==null)
			  leagueId="";
		  String usTTId = (String)request.getParameter("usttrate");
		  if (usTTId==null)
			  usTTId="";
		 /* if (leagueRate.contains("-")){
			
			  temp=leagueRate.split("-");
			  rates.add(temp[0]);
			  rates.add(temp[1]);
		  }else{
			  leagueRate="";
		  }
		  
		 
		  if (usTTRate.contains("-")){
		
			  temp=usTTRate.split("-");
			  rates.add(temp[0]);
			  rates.add(temp[1]);
		  }else{
			  usTTRate="";
		  }*/
	
		  //if not any
		  String low="";
		  String high="";
		  String estimateRate = (String)request.getParameter("estimaterate");
		  if (estimateRate.contains("-")){
			
			  temp=estimateRate.split("-");
			  low=temp[0];
			  high=temp[1];
			  rates.add(temp[0]);
			  rates.add(temp[1]);
		  }else{
			  estimateRate="";
		  }
		  
		  if (rates.size()<1){
			  rates.add("0");
			  rates.add("0");
		  }
		  
		  Collections.sort(rates);
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, "lowrate= "+rates.get(0));
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, "highrate= "+rates.get(rates.size()-1));
		  
		 
		  String playStyle = (String)request.getParameter("playstyle");
		  if (playStyle.contains("any"))
			  playStyle = "any";
		  String rubberType = (String)request.getParameter("rubbertype");
		  if (rubberType.contains("any"))
			  rubberType = "any";
		  String racketStyle = (String)request.getParameter("racketholdstyle");
		  if (racketStyle.contains("any"))
			  racketStyle = "any";
		 
		  /*String Location="";
		  String Space=" ";
		  for (int i=1; i<15; i++){
			  String loc = (String)request.getParameter("location"+i);
			 if (!Location.equals(""))
				 Location=Location + Space;
			 if (loc!=null && loc.equals("on"))
				 Location = Location + map.get("location"+i);
		  }*/
		  String Location=(String)request.getParameter("club");
		  if (Location==null)
			  Location="";
		
			
		  
		/*  String stmt3 = "insert into ppmembers values ("+"'"+userName+"', "+"'"+email+"', "+
		  "'"+racketStyle+"', "+"'"+rubberType+"', "+"'"+playStyle+"', "+"'"+Location+"', "+Integer.parseInt(rates.get(0).toString())+", "
		  +Integer.parseInt(rates.get(rates.size()-1).toString())+", "+"'"+teamname+"'"+", ''"+")";*/
		  
		  String stmt3 = "insert into ppmembers values ("+"'"+userName+"', "+"'"+email+"', "+
		  "'"+racketStyle+"', "+"'"+rubberType+"', "+"'"+playStyle+"', "+"'"+Location+"', "+low+", "
		  +high+", "+"'"+teamname+"'"+", ''"+", '"+usTTId+"',"+" '"+leagueId+"',"+" '"+estimateRate+"'"+", '"+city+"', '"+state+"'"+", '"+country+"'"+", '"+coach+"',"+" '"+level+"'"+", '"+leaguename+"'" +")";
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt3);
		  
		  boolean f1 = false;
		  boolean f2 = false;
		  f1=executeStmt(con, stmt3);
		  String stmt4 = "insert into login values ("+"'"+email+"', "+"'"+pwd+"')";
		  f2=executeStmt(con, stmt4);
		  String keyError = "YES";
		  if (f1 && f2)
			  keyError="NO";
			  
		  con.close();
		  if (keyError.equals("YES")){
			  
			  request.setAttribute("keyError", "Yes");
			  request.getRequestDispatcher("update.jsp").forward(request, response);
			  return;
		  } 
		  
		  String message = "Your profile has been updated.";
		  request.setAttribute("ConfirmMessage", message);
		  request.getRequestDispatcher("confirm.jsp").forward(request, response);
		  
		  
		  
	  }catch(Exception e){
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, "got exception in doMemberUpdate method.");
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, e.getMessage());
	  }
	  
	  
  }
  private void doSelectTask(HttpServletRequest request, HttpServletResponse response){
	  try {
		    ConfigLogUtil.log(ConfigConstants.LOGFILE, "In doSelectTask method.");
		  	String back = (String)request.getParameter("back");
		  	if (back!=null && back.equals("back")){
		  		request.getRequestDispatcher("partner.jsp").forward(request, response);
			  return;
		  	}
		  	String task = (String)request.getParameter("task");
	  
		  if (task.equals("up")){
			  
			  Connection con1 = getConnectTionFromPool();
			  String data = "select * from ppmembers where Email="+"'"+(String)session.getAttribute("sessionMail")+"'";
			  ResultSet profile = executeQueryStmt(con1, data);
			 
			  request.setAttribute("profile", profile);
			  request.setAttribute("con1", con1);
			  
			  
			  request.getRequestDispatcher("update.jsp").forward(request, response);
			  return;
		  }
	 
		  if (task.equals("fp")){
			  Connection con2 = getConnectTionFromPool();
			  String lnames = "select LeagueName from leagues";
			  ResultSet leaguenames = executeQueryStmt(con2, lnames);
			 
			  request.setAttribute("LeagueNames", leaguenames);
			  request.setAttribute("con", con2);
			  request.getRequestDispatcher("setrequirementinfo.jsp").forward(request, response);
			  return; 
		  }
		  if (task.equals("fc")){
			  request.getRequestDispatcher("coachsearching.jsp").forward(request, response);
			  return; 
		  }
		  if (task.equals("pt")){
			  request.getRequestDispatcher("schedule.jsp").forward(request, response);
			  return; 
		  }
		  if (task.equals("ck")){
			  request.getRequestDispatcher("searchposting.jsp").forward(request, response);
			  return;
		  }
		  if (task.equals("go")){
			  Connection icon = getConnectTionFromPool();
			 
			  String mstm = "select LeagueName, LeagueType from leagues where LeagueDirectorMail='"+(String)session.getAttribute("sessionMail")+"'";
			  ConfigLogUtil.log(ConfigConstants.LOGFILE, mstm);
			  ResultSet leagues = executeQueryStmt(icon, mstm);
			  String league = "nothing";
			  String type="";
			  while(leagues.next()){
				  league=leagues.getString(1);
				  type=leagues.getString(2);
			  }
		
			  String tstm = "select Tournament from leaguetournament where LeagueName='"+league+"'";
			  ConfigLogUtil.log(ConfigConstants.LOGFILE, tstm);
			 
			
			  ResultSet tnames = executeQueryStmt(icon, tstm);
			  
			  request.setAttribute("icon", icon);
			  request.setAttribute("tnames", tnames);
			  request.setAttribute("type", type);
			  
			  request.getRequestDispatcher("gameconfig.jsp").forward(request, response);
			  return;
		  }
		  if (task.equals("gp")){
			  Connection econ = getConnectTionFromPool();
			  String estm = "select MatchName from games where CreatedBy='"+(String)session.getAttribute("sessionMail")+"'"+" and Showup='Y'";
			  ConfigLogUtil.log(ConfigConstants.LOGFILE, estm);
			  ResultSet matchname = executeQueryStmt(econ, estm);
			  request.setAttribute("econ", econ);
			  request.setAttribute("matchname", matchname);
			  request.getRequestDispatcher("gameedit.jsp").forward(request, response);
			  return;
		  }
		  if (task.equals("cl")){
			  request.getRequestDispatcher("createleague.jsp").forward(request, response);
			  return;
		  }
		  if (task.equals("ml")){
			  Connection mcon = getConnectTionFromPool();
			  String mstm = "select LeagueName from leagues where LeagueDirectorMail='"+(String)session.getAttribute("sessionMail")+"'";
			  ConfigLogUtil.log(ConfigConstants.LOGFILE, mstm);
			  ResultSet leagues = executeQueryStmt(mcon, mstm);
			  String league = "";
			  while(leagues.next()){
				  league=leagues.getString(1);
			  }
			  if (league.equals("")){
				  String message = "Only league directors can access this page!";
				  request.setAttribute("ConfirmMessage", message);
				  request.getRequestDispatcher("confirm.jsp").forward(request, response);
				  return;
			  }
			  String tstm = "select Tournament from leaguetournament where LeagueName='"+league+"'";
			  ConfigLogUtil.log(ConfigConstants.LOGFILE, tstm);
			  
			  ResultSet tournaments = executeQueryStmt(mcon, tstm);
			  request.setAttribute("tournaments", tournaments);
			  request.setAttribute("con", mcon);
			  request.getRequestDispatcher("manageleague.jsp").forward(request, response);
			  return;
		  }
		  if (task.equals("jl")){
			  Connection jcon = getConnectTionFromPool();
			  String jstm = "select LeagueName from leagues";
			  ResultSet leagues = executeQueryStmt(jcon, jstm);
			 
			  session.setAttribute("leagues", leagues);
			  session.setAttribute("lcon", jcon);
			  request.getRequestDispatcher("joinleague.jsp").forward(request, response);
			  return;
		  }
		  //need to join tournament.
		  if (task.equals("jt")){
			  Connection jcon = getConnectTionFromPool();
			  String jstm = "select Tournament from leaguetournament where LeagueName in (select leagueName from leagues where LeagueDirectorMail='"+(String)session.getAttribute("sessionMail")+"') or Leaguename in (select LeagueName from leaguemembers where MemEmail='"+(String)session.getAttribute("sessionMail")+"')";
			  ResultSet tournaments = executeQueryStmt(jcon, jstm);
			  ConfigLogUtil.log(ConfigConstants.LOGFILE, jstm);
			 
			  session.setAttribute("tournaments", tournaments);
			  session.setAttribute("tcon", jcon);
			  request.getRequestDispatcher("jointournament.jsp").forward(request, response);
			  return;
		  }
		  
		  
		  
		  if (task.equals("gr")){
			  Connection scon = getConnectTionFromPool();
			  String sstm="";
			  if (((String)session.getAttribute("leaguedir")).equals("no")){
				  sstm = "select MatchName from games where Showup='Y' and TournamentName like 'not%'";
			  }else{
				  sstm = "select MatchName from games where Showup='Y' and TournamentName like 'not%' or TournamentName in (select Tournament from leaguetournament where LeagueName='"+(String)session.getAttribute("leaguename") +"')";
			  }
			  ConfigLogUtil.log(ConfigConstants.LOGFILE, sstm);
			  ResultSet matchname = executeQueryStmt(scon, sstm);
			  request.setAttribute("scon", scon);
			  request.setAttribute("matchname", matchname);
			  request.getRequestDispatcher("gameshow.jsp").forward(request, response);
			  return;
		  }
		  if (task.equals("ca")){
			  String Email = (String)session.getAttribute("sessionMail");
			  Connection con2 = getConnectTionFromPool();
			  //Delete old profile.
			  String stmt1 = "delete from login where Email="+"'"+Email+"'";
			  String stmt2 = "delete from ppmembers where Email="+"'"+Email+"'";
			  executeStmt(con2, stmt1);
			  executeStmt(con2, stmt2);
			  con2.close();
			  request.getRequestDispatcher("cancel.html").forward(request, response);
			  return;
		  }
		  
		  //implementing
		  if (task.equals("em")){
			  String name = (String)request.getParameter("mn");
			  String leaguename =(String)session.getAttribute("leaguename");
			  Connection econ = getConnectTionFromPool();
			  String jstm = "select * from leaguemembers where LeagueName='"+leaguename+"' and MemName='"+name+"'";
			  ResultSet meminfo = executeQueryStmt(econ, jstm);
			  ConfigLogUtil.log(ConfigConstants.LOGFILE, jstm);
			  request.setAttribute("meminfo", meminfo);
			  request.setAttribute("econ",econ);
			  request.getRequestDispatcher("editmeminfo.jsp").forward(request, response);
			  return;
		  }
		  if (task.equals("dm")){
			  Connection dcon = getConnectTionFromPool();
			  String name = (String)request.getParameter("mn");
			  String leaguename =(String)session.getAttribute("leaguename");
			  String dstm = "delete from leaguemembers where LeagueName='"+leaguename+"' and MemName='"+name+"'";
			  ConfigLogUtil.log(ConfigConstants.LOGFILE, dstm);
			  executeStmt(dcon, dstm);
			  String message = "The member is deleted!";
			  request.setAttribute("ConfirmMessage", message);
			  request.getRequestDispatcher("confirm.jsp").forward(request, response);
			  
			  return;
		  }
		  //end
		  
		  if (task.equals("ue")){
			  String id = (String)request.getParameter("eid");
			 // String Location = (String)request.getParameter("Location");
			  String Id = id;
			  String Email = (String)session.getAttribute("sessionMail");
			  
			  
			  
			  Connection con = getConnectTionFromPool();
			  
			  
			  String st1 ="select Email from ppmembers a, event b where b.InvitedBy=a.Username and b.Id="+Id;
			  ResultSet ems = executeQueryStmt(con, st1);
			  String em=null;
			  while(ems.next())
				  em = ems.getString(1);
			  String flag="";
			  if (em==null || !em.equals(Email)){
				  
				  request.setAttribute("flag", "error");
				  request.getRequestDispatcher("tasks.jsp").forward(request, response);
			  }
			  request.setAttribute("myMail", Email);
			  request.setAttribute("inviMail", em);
			  
			
				
			  String stmt1= "select b.Username, c.Email, c.Status, a.Time, a.Location from event a, ppmembers b, person c where c.Email=b.Email and a.Id="+Id+" and c.Id="+Id;
			  ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt1);
			
				  ResultSet srs = executeQueryStmt(con, stmt1);
				  
				  String stmt2="select Status from event where Id="+Id;
			      ResultSet event = executeQueryStmt(con, stmt2);
			      
				  request.setAttribute("srs", srs);
				  request.setAttribute("event", event);
				  request.setAttribute("con", con);
				  request.setAttribute("myMail", Email); 
				  
				  session.setAttribute("sessionMail", Email);
				  session.setAttribute("id", Id);
				 // request.setAttribute("Location", Location);
				  request.getRequestDispatcher("status.jsp").forward(request, response);
			  
		  
		  }
	  
	  } catch (Exception e){
		  
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, e.getMessage());
		  
	  }
	  
  }
  
  private void doStatus(HttpServletRequest request, HttpServletResponse response){
	  
	  
	 
	  ConfigLogUtil.log(ConfigConstants.LOGFILE, "In doStatus method.");
	  
	  String Location = (String)request.getParameter("Location");
	  String State ="";
	  String Country="";
	  String Homeclub="";
	  String Name="";
	  if (Location.contains("Any"))
		  Location = "undecided location";
	  String Id = (String)request.getParameter("Id");
	  String Email = (String)request.getParameter("Email");
	  DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	   //get current date time with Date()
	   Date date = new Date();
	   String Dat = dateFormat.format(date);
	   ConfigLogUtil.log(ConfigConstants.LOGFILE, Dat);
	  //String Date = (String)request.getParameter("Date");
	  session.setAttribute("sessionTimeStamp", Dat);
	  String stmt = "select Pwd from login where Email="+"'"+Email+"'";
	  String pwd = "";
	  try {
	  Connection con = getConnectTionFromPool();
	  ResultSet rs = executeQueryStmt(con, stmt);
		while (rs.next())
			pwd = rs.getString(1);
		
	  stmt= "select State, Country, Locations, Username from ppmembers where Email="+"'"+Email+"'";
	  rs = executeQueryStmt(con, stmt);
	  while (rs.next()){
		  State=rs.getString(1);
		  Country=rs.getString(2);
		  Homeclub=rs.getString(3);
		  Name = rs.getString(4);
	  }
	  
	  
	  
	  ConfigLogUtil.log(ConfigConstants.LOGFILE, "Get a db connection");
	 
	  ConfigLogUtil.log(ConfigConstants.LOGFILE, "Got the db connection.");
		
		
	  String stmt1= "select b.Username, c.Email, c.Status, a.Time, a.Location, b.Lowrate, b.Highrate from event a, ppmembers b, person c where c.Email=b.Email and a.Id="+Id+" and c.Id="+Id;
	  ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt1);
	  
		  ResultSet srs = executeQueryStmt(con, stmt1);
		  String stmt2="select Status from event where Id="+Id;
	      ResultSet event = executeQueryStmt(con, stmt2);
	      //get inviter email
	 	 String st1 ="select Email, Username from ppmembers a, event b where b.InvitedBy=a.Username and b.Id="+Id;
		 ResultSet ems = executeQueryStmt(con, st1);
		 String em=null;
		 String inviter = null;
		 while(ems.next()){
			  em = ems.getString(1);
			  inviter = ems.getString(2);
		 }
		 
		  request.setAttribute("event", event);
	  
		  request.setAttribute("srs", srs);
		  request.setAttribute("con", con);
		  request.setAttribute("myMail", Email.toLowerCase());
		  request.setAttribute("inviMail", em.toLowerCase());
		  request.setAttribute("inviter", inviter);
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, "myMail="+Email+" inviMail="+em);
		  session.setAttribute("sessionMail", Email.toLowerCase());
		  session.setAttribute("sessionNation", Country);
		  session.setAttribute("sessionPwd", pwd);
		  session.setAttribute("sessionName", Name);
		  session.setAttribute("sessionState", State);
		  session.setAttribute("sessionLocation", Homeclub);
		  session.setAttribute("id", Id);

			 
			  
		  request.setAttribute("Location", Location);
		  request.getRequestDispatcher("status.jsp").forward(request, response);
	  }catch (Exception e){
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, e.getMessage());
		 
	  }
	//select Username, Status from events a, ppmembers b where a.Email=b.Email and a.Id=30387114987898;
  }
  
  
  private void doSendEmail(HttpServletRequest request, HttpServletResponse response){
	  
	  ConfigLogUtil.log(ConfigConstants.LOGFILE, "In doSendEmail method.");
	  try {
	  String back = (String)request.getParameter("back");
	  if (back!=null && back.equals("back")){
		 
		  request.getRequestDispatcher("setrequirementinfo.jsp").forward(request, response);
		  return;
	  }
	  //String pnumber = (String) request.getParameter("pnumber");
	  String day = (String) request.getParameter("Day");
	  ConfigLogUtil.log(ConfigConstants.LOGFILE, "I am here0");
	  String month = (String)request.getParameter("Month");
	  String year = (String)request.getParameter("Year");
	  String time = (String)request.getParameter("Time");
	  ConfigLogUtil.log(ConfigConstants.LOGFILE, "I am here1");
	  ConfigLogUtil.log(ConfigConstants.LOGFILE, year + " " + month + " " + day + " " + time);
	  String realtime = "";
	  int clock;
	  String[] temptime=null;
	  String[] tmp = null;
	  if (time.contains("AM")){
		  temptime = time.split("AM");
		  
	  	  realtime = temptime[0];
	  }else{
		  temptime = time.split("PM") ;
		  if (temptime[0].contains(":")){
			  tmp = temptime[0].split(":");
			  clock = Integer.parseInt(tmp[0]);
			  clock = clock + 12;
			  realtime = Integer.toString(clock)+":30";
		  }else{
			  
			  clock = Integer.parseInt(temptime[0]);
			  clock = clock + 12;
			  realtime = Integer.toString(clock);
		  }
		  
	  }
	  ConfigLogUtil.log(ConfigConstants.LOGFILE, "here2");
	  String date="";
	  if (realtime.contains(":")){
		  date = year + "-" + month + "-" + day + " " + realtime + ":00";
	  }else{
		  date = year + "-" + month + "-" + day + " " + realtime + ":00:00";
	  }
	  ConfigLogUtil.log(ConfigConstants.LOGFILE, date);
	  
	  //String Message = (String) request.getParameter("Message");
	  //ConfigLogUtil.log(ConfigConstants.LOGFILE, Message);
	  
		  
		 
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, "got email list");
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, (String)(request.getParameter("rowsize")));
		  
		  long id=System.nanoTime();
		
		  String location = (String)request.getParameter("club");
		  if (location!=null && location != ""){
			  
		  }else{
			  location = "undecided place";
		  }
		  String sMail = (String)session.getAttribute("sessionMail");
		  String invitedby = (String)session.getAttribute("sessionName");
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, "my session mail "+sMail);
		  //get time, day, month, year from session
		  Connection con = getConnectTionFromPool();
		  boolean flag = false;
		  String message2 = "";
		  boolean rowSelected = false;
		  String message0 = ConfigConstants.EMAIL_MESSAGE00;
		 
		  
		 // String[] emails = new String[100];
		 // int j=0;

		  
		  String stmt2 = "insert into event values ("+"'"+id+"', "+"'"+"active"+"', "+"'"+date+"', "+"'"+invitedby+"', "+"'"+location+"')";
		  //String stmt3 = "insert into person values ("+"'"+email+"', "+"'"+initStatus+"', "+"'"+id+"')";
		  executeStmt(con, stmt2);
		  for (int i=1; i<=Integer.parseInt((request.getParameter("rowsize"))); i++ ){
			  
			  String email = (String)request.getParameter("name"+i);
			  if (email != null && email !=""){
				  String initStatus = "unknown";
				  rowSelected = true;
				  //check session mail
				  if (email.equals(sMail)){
					  flag = true;
					  initStatus = "come";
					  
					  message2 = ConfigConstants.EMAIL_MESSAGE021;
				  }else{
					  
					  message2 = ConfigConstants.EMAIL_MESSAGE022;
				  }
				  //need check a login email.
				 // String stmt2 = "insert into event values ("+"'"+id+"', "+"'"+time+"', "+"'"+invitedby+"', "+"'"+location+"')";
				  String stmt3 = "insert into person values ("+"'"+id+"', "+"'"+email+"', "+"'"+initStatus+"')";
				 // executeStmt(con, stmt2);
				  executeStmt(con, stmt3);
				  ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt2);
				  ConfigLogUtil.log(ConfigConstants.LOGFILE, "MailAddress= "+email);
				  //set up message variable
				  String message1 = ConfigConstants.EMAIL_MESSAGE01;
				  
				  String message3="http://www.pingpongmatch.com/Mystuff?command=status&Email="+email+"&Id="+id+"&Location="+location+"&Date="+(String)session.getAttribute("sessionTimeStamp");
				  String message4=ConfigConstants.EMAIL_MESSAGE04;
				  String message5="http://www.pingpongmatch.com/Mystuff?command=status&Email="+"dummy"+"&Id="+id+"&Location="+location;
				  String message6=ConfigConstants.EMAIL_MESSAGE06;
				  
				  String message = message2  + message3 + "\n\n"  + "\n" + "\n\n"+message6;
				  ConfigLogUtil.log(ConfigConstants.LOGFILE, message);
				  
				  //message=general message + timestamp;url;email;event id;location
				  //adding to email list
				  //emails[j]= email;
				  String event="on "+date+" id="+id;
				 
				  sendMail(message, email, event);
				//  ConfigLogUtil.log(ConfigConstants.LOGFILE, emails[j]);
				  
				 
				  
			  }
			  
			  
		  }
		  request.setAttribute("selectError", "No");
		  if (!rowSelected){
			  request.setAttribute("selectError", "Yes");
			  request.getRequestDispatcher("setrequirementinfo.jsp").forward(request, response);
			  return;
			  
		  }
		  request.setAttribute("selectError", "No");
		  
		  if (!flag){
			  String email = sMail;
			  String initStatus = "come";
			  String stmt3 = "insert into person values ("+"'"+id+"', "+"'"+email+"', "+"'"+initStatus+"')";
			  //String stmt2 = "insert into events values ("+"'"+id+"', "+"'"+email+"', "+"'"+initStatus+"')";
			  //String stmt2 = "insert into events values ("+"'"+id+"', "+"'"+email+"', "+"'"+initStatus+"', "+"'"+time+"')";
			  executeStmt(con, stmt3);
			  ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt2);
			  ConfigLogUtil.log(ConfigConstants.LOGFILE, "MailAddress= "+email);
			  //set up message variable
			  String message1 = ConfigConstants.EMAIL_MESSAGE11;
			  message2 = ConfigConstants.EMAIL_MESSAGE12;
			  
			  String message3="http://www.pingpongmatch.com/Mystuff?command=status&Email="+email+"&Id="+id+"&Location="+location+"&Date="+(String)session.getAttribute("sessionTimeStamp");
			  String message4=ConfigConstants.EMAIL_MESSAGE14;
			  String message5="http://www.pingpongmatch.com/Mystuff?command=status&Email="+"dummy"+"&Id="+id+"&Location="+location;
			  String Mymessage = message2 + message3 +"\n\n";
			  ConfigLogUtil.log(ConfigConstants.LOGFILE, Mymessage);
			 
			  
		//	  ConfigLogUtil.log(ConfigConstants.LOGFILE, "own="+myemail[0]);
			  String event="on "+date;
			  sendMail(Mymessage, email, event);
		  }
		  
		  con.close();

		  String message = ConfigConstants.CONFIRM_MESSAGE8;
		  request.setAttribute("ConfirmMessage", message);
		  request.getRequestDispatcher("confirm.jsp").forward(request, response);
		 
	  }catch(Exception f){
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, f.getMessage());
	  }
  }
  
  private void doTeam(HttpServletRequest request, HttpServletResponse response){
	  
	  try{
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, "in doTeam method");
		  String tname = (String)request.getParameter("tournament");
		  Connection jcon = getConnectTionFromPool();
		  String tstm0 = "select LeagueType from leagues, leaguetournament where leaguetournament.LeagueName=leagues.LeagueName and leaguetournament.Tournament='"+tname+"'";
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, tstm0);
		  String tstm = "select TeamName from leagueteam where Tournament='"+tname+"'";
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, tstm);
		  ResultSet teams = executeQueryStmt(jcon, tstm);
		 
		  String teamMsg ="";
		  while (teams.next()){
			  if (teamMsg.equals("")){
				  teamMsg=teams.getString(1)+":";
			  }else{
				  teamMsg = teamMsg+teams.getString(1)+":";
			  }
		  }
		  
		  if (teamMsg.equals("")){
			  teamMsg="empty";
		  }
		  
		  ResultSet type = executeQueryStmt(jcon, tstm0);
		  String ltype="";
		  while(type.next()){
			  ltype=type.getString(1);
		  }
		  if (!ltype.equals("Team")){
			  ltype="none";
		  }else{
			  ltype="nottm";
		  }
		  //find game names
		  String tstm5 = "select MatchName from games where TournamentName='"+tname+"'";
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, tstm5);
		  ResultSet games = executeQueryStmt(jcon, tstm5);
		  
		  String gameMsg ="";
		  while (games.next()){
			  if (gameMsg.equals("")){
				  gameMsg=games.getString(1)+":";
			  }else{
				  gameMsg = gameMsg+games.getString(1)+":";
			  }
		  }
		 
		  jcon.close();
		  
		  response.setContentType("text/xml");
		  response.setHeader("Cache-Control", "no-cache");
		  teamMsg=teamMsg+ltype+gameMsg;
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, teamMsg);
		  response.getWriter().write(teamMsg); 
		  
	  }catch (Exception a){
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, a.getMessage());
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, "asshole");
		  
	  }
	  
  }
  
private void doGame(HttpServletRequest request, HttpServletResponse response){
	  
	  try{
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, "in doGame method");
		  String tname = (String)request.getParameter("tournament");
		  //if tname = none return right away.
		  String gameMsg ="";
		  if (tname.equals("none")){
			  gameMsg="none";
			  response.setContentType("text/xml");
			  response.setHeader("Cache-Control", "no-cache");
			  
			  response.getWriter().write(gameMsg);
			  return;
			  
		  }
			  
		  Connection jcon = getConnectTionFromPool();
		  String tstm0 = "select MatchName from games where TournamentName='"+tname+"'";
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, tstm0);
		
		  ResultSet games = executeQueryStmt(jcon, tstm0);
		  
		  
		  while (games.next()){
			  if (gameMsg.equals("")){
				  gameMsg=games.getString(1)+":";
			  }else{
				  gameMsg = gameMsg+games.getString(1)+":";
			  }
		  }
		  
		  //need tournament type info.
		  String tstm1 = "select LeagueType from leagues, leaguetournament where leaguetournament.LeagueName=leagues.LeagueName and leaguetournament.Tournament='"+tname+"'";
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, tstm1);
		  ResultSet type = executeQueryStmt(jcon, tstm1);
		  String ltype="";
		  while(type.next()){
			  ltype=type.getString(1);
		  }
		  if (!ltype.equals("Team")){
			  ltype="NoTm";
		  }else{
			  ltype="YesTm";
		  }
		 
		  jcon.close();
		  
		  response.setContentType("text/xml");
		  response.setHeader("Cache-Control", "no-cache");
		  gameMsg=gameMsg+ltype;
		  response.getWriter().write(gameMsg); 
		  
	  }catch (Exception a){
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, a.getMessage());
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, "asshole");
		  
	  }
	  
  }
  
  private void doSelectMember(HttpServletRequest request, HttpServletResponse response){
	  try{
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, "in doSelectMember");
		  String back = (String)request.getParameter("back");
		  if (back!=null && back.equals("back")){
			  request.getRequestDispatcher("tasks.jsp").forward(request, response);
			  return;
		  }
		 
		 
		  String expectLow = (String) request.getParameter("expectlow");
		  String expectHigh = (String) request.getParameter("expecthigh");
		  if (expectLow.equals("none"))
			  expectLow="0";
		  
		  if (expectHigh.equals("none"))
			  expectHigh="2601";
		  
		//  session.setAttribute("DATE", date);
		//  session.setAttribute("MESSAGE", message);
		  
		  //currentday info
		  String today = (String) request.getParameter("today");
		  String cDay = (String) request.getParameter("currentDay");
		  String cMonth = (String) request.getParameter("currentMonth");
		  String cYear = (String) request.getParameter("currentYear");
		  String cDate = cDay + "-" +cMonth+ "-" +cYear;
		 
		  String Email = (String)session.getAttribute("sessionMail");
		  
		  String  playStyle = "";
		  
		  
		  String  rubberType = "";
		  
		  String rackethold = "";
		  String Location="";
		//  String[] raterange = expectRate.split("-");
		  String lowrate = expectLow;
		  String highrate = expectHigh;
		  String lastname = (String)request.getParameter("lastname");
		  if (lastname  == null)
			  lastname = "";
		  lastname.toLowerCase();
		  String teamname = (String)request.getParameter("teamname");
		  if (teamname==null)
			  teamname="";
		  teamname.toUpperCase();
		  
		  String city = (String)request.getParameter("city");
		  if (city==null)
			  city="";
		  city.toUpperCase();
		  
		  String state = (String)request.getParameter("state");
		  if (state==null)
			  state="";
		  state.toUpperCase();
		  
		  String country = (String)request.getParameter("country");
		  if (country==null)
			  country="";
		  country.toUpperCase();
		  
		  String lname = (String)request.getParameter("LeagueName");
		  if (lname==null || lname.equals("none") || lname.equals("no"))
			  lname="";
		  
		  
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, "lastname="+lastname+" teamname="+teamname+" lowrate="+lowrate+ " highrate="+highrate);
		
		  //Location = (String)request.getParameter("club");
		  Location = "Any";
		
		  request.setAttribute("myLocations", Location);
		  session.setAttribute("myLocations", Location);
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, "play location="+Location);
		  
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, "before loop");
		  Connection con = getConnectTionFromPool();
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, "I passed getConnection2.");
		  
		  //check one who wants to play today.
		  String stmt1="";
		  if (today != null && today.equals("yes")){
			  
			  stmt1="select * from ppmembers where Lowrate >= "+lowrate+ " and Highrate <= "+highrate+ " and Rubberstyle like '%"+rubberType+"' and LeagueName like '%"+lname+"%'"+" and Playstyle like '%"+playStyle+"' and Racketstyle like '%"+rackethold+"' and Username like '%"+lastname+"' and TeamName like '%"+teamname+"%'"+ " and City like '%"+city+"%'"+" and State like '%"+state+"%'" +" and Country like '%"+country+"%'" + " and Today='"+cDate+"'"+ " and Email != '"+" order by Highrate";
			  
			  
			  
		  }else{
			  stmt1="select * from ppmembers where Lowrate >= "+lowrate+ " and Highrate <= "+highrate+ " and  LeagueName like '%"+lname+"%'"+" and Username like '%"+lastname+"' and City like '%"+city+"%'" +" and State like '%"+state+"%'"+" and Country like '%"+country+"%'"+ " order by Highrate";
			  
		  }
		  
			
			
		 
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt1);
		  ResultSet rs = executeQueryStmt(con, stmt1);
		  
		  //set a record for today's play
		  if (today != null && today.equals("yes")){
			  Connection con1 = getConnectTionFromPool();
			  String st = "update ppmembers set Today="+"'"+cDate+"'"+" where Email='"+session.getAttribute("sessionMail")+"'";
			  ConfigLogUtil.log(ConfigConstants.LOGFILE, st);
			  executeStmt(con1, st);
			  con1.close();
		  }
		  
		  session.setAttribute("DBResults", rs); 
		  session.setAttribute("ResultCon", con);
		  session.setAttribute("selectedState", state);
		  request.getRequestDispatcher("results.jsp").forward(request, response);
		
		  //con.close();
		  
	  }catch (Exception a){
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, a.getMessage());  
		  
	  }
	  
  }
  
  private void doSelectCoach(HttpServletRequest request, HttpServletResponse response){
	  try{
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, "in doSelectCoach");
		  String back = (String)request.getParameter("back");
		  if (back!=null && back.equals("back")){
			  request.getRequestDispatcher("tasks.jsp").forward(request, response);
			  return;
		  }
		 
		 
		  String expectLow = (String) request.getParameter("expectlow");
		  String expectHigh = (String) request.getParameter("expecthigh");
		  if (expectLow.equals("none"))
			  expectLow="0";
		  
		  if (expectHigh.equals("none"))
			  expectHigh="2601";
		  
		//  session.setAttribute("DATE", date);
		//  session.setAttribute("MESSAGE", message);
		  
		  //currentday info
		  /*String today = (String) request.getParameter("today");
		  String cDay = (String) request.getParameter("currentDay");
		  String cMonth = (String) request.getParameter("currentMonth");
		  String cYear = (String) request.getParameter("currentYear");
		  String cDate = cDay + "-" +cMonth+ "-" +cYear;*/
		 
		  String Email = (String)session.getAttribute("sessionMail");
		  
		  String  playStyle = "";
		  
		  
		  String  rubberType = "";
		  
		  String rackethold = ""; 
		  String Location="";
		//  String[] raterange = expectRate.split("-");
		  String lowrate = expectLow;
		  String highrate = expectHigh;
		  String lastname = (String)request.getParameter("lastname");
		  if (lastname  == null)
			  lastname = "";
		  lastname.toLowerCase();
		String teamname = (String)request.getParameter("teamname");
		  if (teamname==null)
			  teamname="";
		  teamname.toUpperCase();
		  
		  String certified = (String)request.getParameter("certified");
		  if (certified.equals("No"))
			  certified="";
		  certified.toUpperCase();
		  
		  String city = (String)request.getParameter("city");
		  if (city==null)
			  city="";
		  city.toUpperCase();
		  
		  String state = (String)request.getParameter("state");
		  if (state==null)
			  state="";
		  state.toUpperCase();
		  
		  String country = (String)request.getParameter("country");
		  if (country==null)
			  country="";
		  country.toUpperCase();
		  
		 // ConfigLogUtil.log(ConfigConstants.LOGFILE, "lastname="+lastname+" teamname="+teamname+" lowrate="+lowrate+ " highrate="+highrate);
		
		  Location = (String)request.getParameter("club");
		
		  request.setAttribute("CmyLocations", Location);
		  session.setAttribute("CmyLocations", Location);
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, "play location="+Location);
		  
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, "before loop");
		  Connection con = getConnectTionFromPool();
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, "I passed getConnection2.");
		  
		  //check one who wants to play today.
		  String stmt1="";
		 /* if (today != null && today.equals("yes")){
			  
			  stmt1="select * from ppmembers where Lowrate >= "+lowrate+ " and Highrate <= "+highrate+ " and Rubberstyle like '%"+rubberType+"' and Playstyle like '%"+playStyle+"' and Racketstyle like '%"+rackethold+"' and Username like '%"+lastname+"' and TeamName like '%"+teamname+"%'"+ " and City like '%"+city+"%'"+" and State like '%"+state+"%'" +" and Country like '%"+country+"%'" + " and Today='"+cDate+"'"+ " and Email != '"+Email+"'";
			  
			  
			  
		  }else{*/
			  stmt1="select * from ppmembers where Lowrate >= "+lowrate+ " and Highrate <= "+highrate+ " and Rubberstyle like '%"+rubberType+"' and Playstyle like '%"+playStyle+"' and Racketstyle like '%"+rackethold+"' and Username like '%"+lastname+"' and Level like '%"+certified+"%'"+ " and City like '%"+city+"%'" +" and State like '%"+state+"%'"+" and Country like '%"+country+"%'"+ " and Coach = '"+"Yes"+"'"+" order by Highrate";
			  
		 // }
		  
			
			
		 
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt1);
		  ResultSet rs = executeQueryStmt(con, stmt1);
		  
		  //set a record for today's play
		/* if (today != null && today.equals("yes")){
			  Connection con1 = getConnectTionFromPool();
			  String st = "update ppmembers set Today="+"'"+cDate+"'"+" where Email='"+session.getAttribute("sessionMail")+"'";
			  ConfigLogUtil.log(ConfigConstants.LOGFILE, st);
			  executeStmt(con1, st);
			  con1.close();
		  }*/
		  
		  session.setAttribute("CDBResults", rs); 
		  session.setAttribute("CResultCon", con);
		  session.setAttribute("CselectedState", state);
		  request.getRequestDispatcher("coachresults.jsp").forward(request, response);
		
		  //con.close();
		  
	  }catch (Exception a){
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, a.getMessage());  
		  
	  }
	  
  }

  private void doMemberInfo(HttpServletRequest request, HttpServletResponse response){
	  Vector rates=new Vector();
	  String[] temp1;
	  String[] temp2;
	  String[] temp3;
	  try{
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, "in doMemberinfo");
		  String back = (String)request.getParameter("back");
		  if (back!=null && back.equals("back")){
			  request.getRequestDispatcher("partner.jsp").forward(request, response);
			  return;
		  }
		  String coach = (String)request.getParameter("coach");
		  String level = (String)request.getParameter("certified");
		  String lastName= (String)request.getParameter("lname");
		  String firstName= (String)request.getParameter("fname");
		  String teamname = (String)request.getParameter("teamname");
		  String leaguename = (String)request.getParameter("leaguename");
		  String country = (String)request.getParameter("country");
		  String state = (String)request.getParameter("state");
		  if (teamname == null)
			  teamname="";
		  teamname.toUpperCase();
		  if (leaguename == null)
			  leaguename="";
		  leaguename.toUpperCase();
		  String city = (String)request.getParameter("city");
		  if (city == null)
			  city="";
		  city.toUpperCase();
		  //country.toUpperCase();
		  //state.toUpperCase();
		  String userName= firstName + " " + lastName;
		  userName = userName.toLowerCase();
		 // String lastName= (String)request.getParameter("lname");
		 // String firstName= (String)request.getParameter("fname");
		  String email = (String)request.getParameter("email");
		 
		  
		  String pwd = (String)request.getParameter("pwd");
		  String rpwd = (String)request.getParameter("rpwd");
		  String pwderror = "No";
		  if (!pwd.equals(rpwd)){
			  
			  request.setAttribute("pwdError", "Yes");
			  request.getRequestDispatcher("memberinfo.jsp").forward(request, response);
			  return;
		  } 
		  
		  String leagueId = (String) request.getParameter("leaguerate");
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, leagueId);
		  if(leagueId==null)
			  leagueId="";
		  String usTTId = (String)request.getParameter("usttrate");
		  if (usTTId==null)
			  usTTId="";
		/*  if (leagueRate.contains("-")){
			
			  temp1=leagueRate.split("-");
			
			  rates.add(Integer.parseInt(temp1[0]));
			  rates.add(Integer.parseInt(temp1[1]));
		  }else{
			  leagueRate="";
		  }
		  
		  
		  String usTTRate = (String)request.getParameter("usttrate");
		  if (usTTRate.contains("-")){
		
			  temp2=usTTRate.split("-");
			  rates.add(Integer.parseInt(temp2[0]));
			  rates.add(Integer.parseInt(temp2[1]));
		  }else{
			  usTTRate="";
		  }*/
	
		  //if not any
		  String estimateRate = (String)request.getParameter("estimaterate");
		  String low = "";
		  String high = "";
		  if (estimateRate.contains("-")){
			
			  temp3=estimateRate.split("-");
			  low = temp3[0];
			  high = temp3[1];
			  rates.add(Integer.parseInt(temp3[0]));
			  rates.add(Integer.parseInt(temp3[1]));
		  }else{
			  estimateRate="";
		  }
		  
		  if (rates.size()<1){
			  rates.add(0);
			  rates.add(0);
		  }
		  
		  Collections.sort(rates);
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, "lowrate= "+rates.get(0));
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, "highrate= "+rates.get(rates.size()-1));
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, "ratesize= "+rates.size());
		  
		 
		 // String playStyle = (String)request.getParameter("playstyle");
		//  if (playStyle.contains("any"))
			String  playStyle = "any";
		 // String rubberType = (String)request.getParameter("rubbertype");
		 // if (rubberType.contains("any"))
			 String rubberType = "any";
		//  String racketStyle = (String)request.getParameter("racketholdstyle");
		 // if (racketStyle.contains("any"))
		String	  racketStyle = "any";
		  
		  
		 // String Location=(String)request.getParameter("club");
		String Location="";
		  /*e=" ";
		  for (int i=1; i<15; i++){
			  String loc = (String)request.getParameter("location"+i);
			 if (!Location.equals(""))
				 Location=Location + Space;
			 if (loc!=null && loc.equals("on"))
				 Location = Location + map.get("location"+i);
		  }*/
		  
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, "before loop");
		  Connection con = getConnectTionFromPool();
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, "I passed getConnection2.");
		  
		  
		 	
		  
		  String stmt1 = "insert into ppmembers values ("+"'"+userName+"', "+"'"+email+"', "+
		  "'"+racketStyle+"', "+"'"+rubberType+"', "+"'"+playStyle+"', "+"'"+Location+"', "+low+", "
		  +high+", "+"'"+teamname+"'"+", ''"+", '"+usTTId+"',"+" '"+leagueId+"',"+" '"+estimateRate+"'"+", '"+city+"'"+", '"+state+"'"+", '"+country+"'"+", '"+coach+"',"+" '"+level+"', "+"'"+leaguename+"')";
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt1);
		  boolean f1 = false;
		  boolean f2 = false;
		  f1=executeStmt(con, stmt1);
		  String stmt2 = "insert into login values ("+"'"+email+"', "+"'"+pwd+"')";
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt2);
		  f2=executeStmt(con, stmt2);

		//  
		  String keyError = "YES";
		  if (f1 && f2){
			  ConfigLogUtil.log(ConfigConstants.LOGFILE, "both return true.");
			  keyError="NO";
			  request.setAttribute("keyError", "No");
		  }
			  
		  con.close();
		  if (keyError.equals("YES")){
			  
			  request.setAttribute("keyError", "Yes");
			  request.getRequestDispatcher("partner.jsp").forward(request, response);
			  return;
		  } 
		  
		  pwdValid=1;
		  String cDay = (String) request.getParameter("currentDay");
		  
			 session.setAttribute("sessionDay", cDay);
			  String cMonth = (String) request.getParameter("currentMonth");
			  session.setAttribute("sessionMonth", cMonth);
			  String cYear = (String) request.getParameter("currentYear");
			  session.setAttribute("sessionTimeStamp", cYear+"-"+cMonth+"-"+cDay);
			  session.setAttribute("sessionYear", cYear);
			  session.setAttribute("sessionMail", email);
			  session.setAttribute("sessionPwd", pwd);
		  
		  String message = ConfigConstants.SIGNIN_MESSAGE;
		  request.setAttribute("ConfirmMessage", message);
		  String content = ConfigConstants.SIGNIN_CONTENT1+
				  email+ConfigConstants.SIGNIN_CONTENT2;
		  String title = ConfigConstants.SIGNIN_TITLE;
		  sendMail(content, email, title);
		  
		request.getRequestDispatcher("regconfirm.jsp").forward(request, response);
		  
	  }catch(Exception e){
		  
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, e.getMessage());
	  }
  }
  
private void doStatusUpdate(HttpServletRequest request, HttpServletResponse response){
	try {
		
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "get into doStatusUpdate method");
		
		String done = (String)request.getParameter("done");
		if (done != null && done.equals("exit")){
			
			request.getRequestDispatcher("end1.html").forward(request, response);
			return;
		}
		String message = (String)request.getParameter("Message");
		String smail = (String)session.getAttribute("sessionMail");
		
		
		String res = (String)request.getParameter(smail);
		if (res.equals("not respond"))
			res="unknown";
		String value = (String)request.getParameter("task");
		String stat = "";
		
		if (value.equals("ac"))
			stat="active";
		if (value.equals("co"))
			stat="confirm";
		if (value.equals("ca"))
			stat="cancel";
		if (value.equals("do"))
			stat="done";	
		
		//String Location = (String)request.getParameter("Location");
		Connection con = getConnectTionFromPool();
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "get last connections.");
		String id = (String)session.getAttribute("id");
			
		String stmt	= "update event set Status='"+stat+"' where Id='"+id+"'";
		
		ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt); 
		  
		executeStmt(con, stmt);
		  
		String stmt1= "update person set Status='"+res+"' where Email='"+smail+"' and Id='"+id+"'";
			
		ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt1); 
		  
		executeStmt(con, stmt1);
		//send message if status updated.
		String stmt2 = "select Location, Time from event where Id='"+id+"'";
		ResultSet locs = executeQueryStmt(con, stmt2);
		String location="";
		String time="";
		while (locs.next()){
			location = locs.getString(1);
			time = locs.getString(2);
		}
		String stmt3 = "select Email from person where Id='"+id+"'";
		ResultSet ems = executeQueryStmt(con, stmt3);
		String event = "on "+time+ " update alert!";
		while (ems.next()){
			String up = "status updated \n"+message+"\n\n";
			String finish="";
			if (stat.equals("cancel")||stat.equals("done"))
				finish = ConfigConstants.CONFIRM_MESSAGE7;
			
			String link= up + finish + "http://www.pingpongmatch.com/Mystuff?command=status&Email="+ems.getString(1)+"&Id="+id+"&Location="+location+"&Date="+(String)session.getAttribute("sessionTimeStamp");
			
			ConfigLogUtil.log(ConfigConstants.LOGFILE, message+"no space");
			if (!message.substring(message.length()-1).equals(":"))
				sendMail(link, ems.getString(1), event);
		} 
		con.close();
		//ConfigLogUtil.log(ConfigConstants.LOGFILE, b);
	//set the status
		
		ConfigLogUtil.log(ConfigConstants.LOGFILE, smail);
		ConfigLogUtil.log(ConfigConstants.LOGFILE, res);
		String message4 = ConfigConstants.CONFIRM_MESSAGE1;
		request.setAttribute("ConfirmMessage", message4);
		request.getRequestDispatcher("confirm.jsp").forward(request, response);
	}catch(Exception e){
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, "exception in updateStatus");
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, e.getMessage());
	  }
	
	
}
//
private void doGameShow(HttpServletRequest request, HttpServletResponse response){
	ConfigLogUtil.log(ConfigConstants.LOGFILE, "get into doGameShow method");
	try{
		//get group number from previous page.
		//Updating rating case
		
		if (((String)request.getParameter("submit")).equals("Update")){

			doSingleReport(request, response);
			return;

		}
		
		//end
		int groupnum = Integer.parseInt((String)request.getParameter("gnum"));
		if (groupnum==0)
			groupnum=1;
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "group number:"+Integer.toString(groupnum));
		String button = (String)request.getParameter("submit");
		ConfigLogUtil.log(ConfigConstants.LOGFILE, button);

		String matchname = (String)request.getParameter("gamename");

		String desc = (String)request.getParameter("description");
		//String leaguename= (String)session.getAttribute("leaguename");
		String leaguename= (String)request.getAttribute("leaguename");
	//	String director = (String)session.getAttribute("no");
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "matchname:" + matchname);
		Connection con = getConnectTionFromPool();
		String stm1 ="select NumOfGroup, PlayType, BracketNum from games where MatchName='"+matchname+"'";
		ConfigLogUtil.log(ConfigConstants.LOGFILE, stm1);
		ResultSet rs = executeQueryStmt(con, stm1);
		int ng=0;
		String playtype = "";
		int braket = 0;
		while (rs.next()){
			
			
			ng = rs.getInt(1);
			playtype = rs.getString(2);
			braket = rs.getInt(3);
		}
		if (ng==0){
			 con.close();
			String mg="The match can not be found.";
			request.setAttribute("ConfirmMessage", mg);
			request.getRequestDispatcher("confirm.jsp").forward(request, response);
			return;
			
		}
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "number of group:"+Integer.toString(ng));
		
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "format:"+playtype);
		
		//////Check Rate Count/////////////////////////////////
		String ratestmt = "select RateCount from games where MatchName='"+matchname+"'";
		ResultSet counts = executeQueryStmt(con, ratestmt);
		int countChecker=0;
		while (counts.next()){
			
			
			countChecker = counts.getInt(1);
			
		}
		
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "rate count: "+Integer.toString(countChecker));
		//////////////////////////////////////////////////////////////////////////////////////
		if (playtype.equals("Play off")){
			//braketnum = ((Integer)session.getAttribute("BracketNum")).intValue();
			
			String stm03 = "select count(*) from players where MatchName='"+matchname+"'"+" and GroupNumber=1"+" and BracketNumber=1";
			ConfigLogUtil.log(ConfigConstants.LOGFILE, stm03);
			ResultSet rs03 = executeQueryStmt(con, stm03);
			int post = 0;
			while (rs03.next()){
				
				
				post = rs03.getInt(1);
			}
			ConfigLogUtil.log(ConfigConstants.LOGFILE, "size of group:"+Integer.toString(post));
			session.setAttribute("TotalNum", post);
		
			
			String stm02="select PlayerName, Position, BracketNumber, Scores from players where MatchName='"+matchname+"'"+" and GroupNumber=1";
			ConfigLogUtil.log(ConfigConstants.LOGFILE, stm02);
			ResultSet rs02 = executeQueryStmt(con, stm02);
			
			String[][] playerlist = new String[100][100];
			String[][] score = new String[100][100];
			//String[] playoffinit = new String[100];
			String[] padjust1 = new String[100];
			String[] padjust2 = new String[100];
			String[] padjust3 = new String[100];
			
			while (rs02.next()){
				String name = rs02.getString(1);
				int position = rs02.getInt(2);
				int bracket = rs02.getInt(3);
				String sc = rs02.getString(4);
				ConfigLogUtil.log(ConfigConstants.LOGFILE, "Name:"+name+" Position:"+Integer.toString(position)+" Bracket:"+Integer.toString(bracket)+" Score:"+sc);
				playerlist[bracket][position]=name.toLowerCase();
				score[bracket][position]=sc;
				
				
				
			}
			if (countChecker == 1) {
			//phase1
			HashMap<String, Integer> initR = new HashMap<String, Integer>();
			String stoff = "select distinct leaguemembers.MemName, InitRate from players, leaguemembers, indivitournament where Leaguemembers.MemEmail in (select Email from indivitournament) and leaguemembers.MemName in (select players.PlayerName from players where players.MatchName='"+matchname+"')"+" and GroupNumber=1 and BracketNumber=1";
			ConfigLogUtil.log(ConfigConstants.LOGFILE, stoff);
			ResultSet roff = executeQueryStmt(con, stoff);
			while (roff.next()){
				
				String p = roff.getString(1).toLowerCase();
				int r = roff.getInt(2);
				initR.put(p, r);
				ConfigLogUtil.log(ConfigConstants.LOGFILE, p+"!!!!!!!!!!");
				ConfigLogUtil.log(ConfigConstants.LOGFILE, "i here0");
				
			}
			
			HashMap<String, Integer> ad1 = new HashMap<String, Integer>(initR);
			
			//adju1 (name and rates);
			//init rate (name and rates)
			String lastWinner="";
			int p2 = post/2;
			//adjul=init;
			//for (int b=1; b<= playlist.length; b++){
			//	String name = playlist[b];
			HashMap<String, Integer> highest = new HashMap<String, Integer>(initR);
			HashMap<String, Integer> lowest = new HashMap<String, Integer>(initR);
			
			ConfigLogUtil.log(ConfigConstants.LOGFILE, "i here1");
			//go through highest to do adjust in ajustlist.
			Iterator h = highest.entrySet().iterator();
			
			while (h.hasNext()) {
				Map.Entry hEntry = (Map.Entry) h.next();
				String nm = (String)hEntry.getKey();
				highest.put(nm, 0);
				lowest.put(nm, 0);
				ConfigLogUtil.log(ConfigConstants.LOGFILE, nm);
				ConfigLogUtil.log(ConfigConstants.LOGFILE, "i here2");
			}
			ConfigLogUtil.log(ConfigConstants.LOGFILE, "start phase1");
			for (int z=2; z<=braket; z++){
				int x=0;
					for (int m=1; m<=p2; m++){
					
						if (score[z][m].contains("W")||score[z][m].contains("w")){
						
							String winner = playerlist[z][m];
							lastWinner = winner;
						//if m is odd, n=m+1, if m is even, n=m-1
							String loser="";
							if (playerlist[z-1][m+x].equals(winner)){
						
								loser = playerlist[z-1][m+x+1];
							}else{
								loser = playerlist[z-1][m+x];
								
							}
							ConfigLogUtil.log(ConfigConstants.LOGFILE, "out area1");
							ConfigLogUtil.log(ConfigConstants.LOGFILE, winner+"!!!!");
							ConfigLogUtil.log(ConfigConstants.LOGFILE, loser+"!!!!");
							 if (loser.equals("bye")){
								
								 x++;
								 continue;
							 }
							ConfigLogUtil.log(ConfigConstants.LOGFILE, "problem area");
							
							ConfigLogUtil.log(ConfigConstants.LOGFILE, "int loser "+initR.get(loser).toString());
							ConfigLogUtil.log(ConfigConstants.LOGFILE, "int winner "+initR.get(winner).toString());
							ConfigLogUtil.log(ConfigConstants.LOGFILE, "highest loser "+highest.get(loser).toString());
							ConfigLogUtil.log(ConfigConstants.LOGFILE, "highest winner "+highest.get(winner).toString());
							if (initR.get(loser)>highest.get(winner))
								highest.put(winner, initR.get(loser));
							
							lowest.put(loser, initR.get(winner));
							ConfigLogUtil.log(ConfigConstants.LOGFILE, "set highest lowest");
						   
							int delta = functionWinAndLoss(initR, winner, loser);
							ConfigLogUtil.log(ConfigConstants.LOGFILE, "done delta");
							int worg=ad1.get(winner);
							ad1.put(winner, worg+delta);
							ConfigLogUtil.log(ConfigConstants.LOGFILE, "ad1 "+ad1.get(winner));
							int lorg = ad1.get(loser);
							ad1.put(loser, lorg-delta);
							ConfigLogUtil.log(ConfigConstants.LOGFILE, "ad1 "+ad1.get(loser));
							x++;
							
						}
						
						ConfigLogUtil.log(ConfigConstants.LOGFILE, "done internal loop");
					
					}
					p2=p2/2;
				}
			//go through adjustl and inilist to do adjust in ajustlist.
				Iterator iter = initR.entrySet().iterator();
				ConfigLogUtil.log(ConfigConstants.LOGFILE, "i here3"); 
				while (iter.hasNext()) {
					Map.Entry mEntry = (Map.Entry) iter.next();
					int iniRate = (Integer)mEntry.getValue();
					String iniKey = (String)mEntry.getKey();
					int ad1rate = ad1.get(iniKey);
					if ((ad1rate-iniRate) <= 50 ){
						ad1.put(iniKey, iniRate);
					}
					
					
						
					if ((ad1rate-iniRate) > 75 && !lastWinner.equals(iniKey))	{
						
						int adrate = (highest.get(iniKey)+lowest.get(iniKey))/4+ad1.get(iniKey)/2;
						ad1.put(iniKey, adrate);
						
					}
					
					if (iniRate==0)
						ad1.put(iniKey, 0);
				}
				
				ConfigLogUtil.log(ConfigConstants.LOGFILE, "lastWinner1="+lastWinner);
				
			//print out result for phase 1
				ConfigLogUtil.log(ConfigConstants.LOGFILE, "Checking after phase1"); 
				HashMap<String, Integer> temp1 = new HashMap<String, Integer>(ad1);
				Iterator tmpiter1 = temp1.entrySet().iterator();
				while (tmpiter1.hasNext()) {
					Map.Entry tem1Entry = (Map.Entry) tmpiter1.next();
					int tmp1Rate = (Integer)tem1Entry.getValue();
					String tmp1Key = (String)tem1Entry.getKey();
					ConfigLogUtil.log(ConfigConstants.LOGFILE, "iniKey="+tmp1Key);
					ConfigLogUtil.log(ConfigConstants.LOGFILE, "iniRate="+Integer.toString(tmp1Rate)); 
				}
				ConfigLogUtil.log(ConfigConstants.LOGFILE, "Checking done after phase1"); 
			//end phase1
				
			//phase2
				ConfigLogUtil.log(ConfigConstants.LOGFILE, "start phase2");
				Iterator ad1loop = ad1.entrySet().iterator();
				p2 = post/2;
				while (ad1loop.hasNext()){
					
					Map.Entry ad1Entry = (Map.Entry) ad1loop.next();
					String key = (String)ad1Entry.getKey();
					int rating = (Integer)ad1Entry.getValue();
					if (rating != 0)
						continue;
					
					
					
				
					
					//	continue;
					for (int z=2; z<=braket; z++){
						int x=0;
						for (int m=1; m<=p2; m++){
						
							if (score[z][m].contains("W")||score[z][m].contains("w")){
							
								String winner1 = playerlist[z][m];
								String loser1="";
								if (playerlist[z-1][m+x].equals(winner1)){
									
									loser1 = playerlist[z-1][m+x+1];
								}else{
									loser1 = playerlist[z-1][m+x];
									
								}
								ConfigLogUtil.log(ConfigConstants.LOGFILE, "out area2");
								
								 if (loser1.equals("bye")){
									 x++;
									 continue;
								 }
								 
								 if (key.equals(winner1)){
								
									 	if (initR.get(loser1)>highest.get(winner1))
									 		highest.put(winner1, initR.get(loser1));
								 }
								
								
								if (key.equals(loser1))
								
									lowest.put(loser1, initR.get(winner1));
								
								x++;
							}
							
						}
						p2=p2/2;
					}
					
				}
				//go through adjustl and inilist to do adjust in ajustlist.
				Iterator iter2 = initR.entrySet().iterator();
				 
				while (iter2.hasNext()) {
					Map.Entry mEntry2 = (Map.Entry) iter2.next();
					int iniRate2 = (Integer)mEntry2.getValue();
					String iniKey2 = (String)mEntry2.getKey();
					
					
					int unrate=0;
					if (iniRate2==0){
						unrate = (highest.get(iniKey2)+lowest.get(iniKey2))/2;
						initR.put(iniKey2, unrate);
					}
				}
				
				//print out result for phase 2
				ConfigLogUtil.log(ConfigConstants.LOGFILE, "Checking after phase2");
				Connection rcon = getConnectTionFromPool();
				//String clean = "Delete from temprating where LeagueName='"+leaguename+"'";
				//executeStmt(rcon, clean);
				//String rstat = "insert into temprating values ('leaguename', 'MatchName', 'tmp2Key', tmp2Rate, 0)";
				HashMap<String, Integer> temp2 = new HashMap<String, Integer>(initR);
				Iterator tmpiter2 = temp2.entrySet().iterator();
				while (tmpiter2.hasNext()) {
					Map.Entry tem2Entry = (Map.Entry) tmpiter2.next();
					int tmp2Rate = (Integer)tem2Entry.getValue();
					
					String tmp2Key = (String)tem2Entry.getKey();
					if ((String)session.getAttribute("leaguedir")!=null && !((String)session.getAttribute("leaguedir")).equals("no")){
						String rstat = "insert into temprating values ('"+leaguename+"', '"+matchname+"', '"+tmp2Key+"', '"+tmp2Rate+"', 0)";
						executeStmt(rcon, rstat);
					}
					ConfigLogUtil.log(ConfigConstants.LOGFILE, "iniKey="+tmp2Key);
					ConfigLogUtil.log(ConfigConstants.LOGFILE, "iniRate="+Integer.toString(tmp2Rate));
					//ConfigLogUtil.log(ConfigConstants.LOGFILE, "iniRate="+tmp2Rate); 
				}
				ConfigLogUtil.log(ConfigConstants.LOGFILE, "Checking done after phase2"); 
			//end phase2
				
				
				
				
			//phrase 3
				HashMap<String, Integer> ad3 = new HashMap<String, Integer>(initR);
				
				int p3 = post/2;
				ConfigLogUtil.log(ConfigConstants.LOGFILE, "start phase3");
				for (int z=2; z<=braket; z++){
					int x=0;
					for (int m=1; m<=p3; m++){
					
						if (score[z][m].contains("W")||score[z][m].contains("w")){
						
							String winner = playerlist[z][m];
							lastWinner=winner;
						//if m is odd, n=m+1, if m is even, n=m-1
							String loser="";
							if (playerlist[z-1][m+x].equals(winner)){
								
								loser = playerlist[z-1][m+x+1];
							}else{
								loser = playerlist[z-1][m+x];
								
							}
							ConfigLogUtil.log(ConfigConstants.LOGFILE, "out area3");
							if (loser.equals("bye")){
								x++;
								continue;
							}
						    	
							if (initR.get(loser)>highest.get(winner))
								highest.put(winner, initR.get(loser));
							
							lowest.put(loser, initR.get(winner));
							
						    
							int delta = functionWinAndLoss(initR, winner, loser);
							
							int worg=ad3.get(winner);
							ad3.put(winner, worg+delta);
							
							int lorg = ad3.get(loser);
							ad3.put(loser, lorg-delta);
							
							x++;
						}
						
					
					}
					p3=p3/2;
					ConfigLogUtil.log(ConfigConstants.LOGFILE, "here0");
				}
				ConfigLogUtil.log(ConfigConstants.LOGFILE, "here1");
			//go through adjustl and inilist to do adjust in ajustlist.
				Iterator iter3 = initR.entrySet().iterator();
				 
				while (iter3.hasNext()) {
					Map.Entry mEntry3 = (Map.Entry) iter3.next();
					int iniRate3 = (Integer)mEntry3.getValue();
					String iniKey3 = (String)mEntry3.getKey();
					ConfigLogUtil.log(ConfigConstants.LOGFILE, "inikey "+iniKey3);
					ConfigLogUtil.log(ConfigConstants.LOGFILE, "rate "+Integer.toString(iniRate3));
					int ad3rate = ad3.get(iniKey3);
					ConfigLogUtil.log(ConfigConstants.LOGFILE, "ad3 "+ad3.get(iniKey3));
					if ((ad3rate-iniRate3) < 50 ){
						ad3.put(iniKey3, iniRate3);
					}
					
					
					ConfigLogUtil.log(ConfigConstants.LOGFILE, "here2");	
					if ((ad3rate-iniRate3) > 75 && !lastWinner.equals(iniKey3))	{
						
						int adrate = (highest.get(iniKey3)+lowest.get(iniKey3))/4+ad3.get(iniKey3)/2;
						ad3.put(iniKey3, adrate);
						ConfigLogUtil.log(ConfigConstants.LOGFILE, "ad3 "+ad3.get(iniKey3));
					}
					
					ConfigLogUtil.log(ConfigConstants.LOGFILE, "here3");
					
					if (iniRate3==0)
						ad3.put(iniKey3, 0);
				}
				ConfigLogUtil.log(ConfigConstants.LOGFILE, "lastWinner3="+lastWinner);
				//print out result for phase 3
				ConfigLogUtil.log(ConfigConstants.LOGFILE, "Checking after phase3"); 
				HashMap<String, Integer> temp3 = new HashMap<String, Integer>(ad3);
				Iterator tmpiter3 = temp3.entrySet().iterator();
				while (tmpiter3.hasNext()) {
					Map.Entry tem3Entry = (Map.Entry) tmpiter3.next();
					int tmp3Rate = (Integer)tem3Entry.getValue();
					String tmp3Key = (String)tem3Entry.getKey();
					ConfigLogUtil.log(ConfigConstants.LOGFILE, "iniKey="+tmp3Key);
					ConfigLogUtil.log(ConfigConstants.LOGFILE, "iniRate="+Integer.toString(tmp3Rate));
					//ConfigLogUtil.log(ConfigConstants.LOGFILE, "iniRate="+tmp3Rate); 
				}
				ConfigLogUtil.log(ConfigConstants.LOGFILE, "Checking done after phase3"); 
			//end phase3
				
				
			//phrase 4
			HashMap<String, Integer> ad4 = new HashMap<String, Integer>(ad3);
			
			int p4 = post/2;
			ConfigLogUtil.log(ConfigConstants.LOGFILE, "start phase4");
			for (int z=2; z<=braket; z++){
				int x=0;
				for (int m=1; m<=p4; m++){
				
					if (score[z][m].contains("W")||score[z][m].contains("w")){
					
						String winner = playerlist[z][m];
					//if m is odd, n=m+1, if m is even, n=m-1
						String loser="";
						ConfigLogUtil.log(ConfigConstants.LOGFILE, "in area");
						if (playerlist[z-1][m+x].equals(winner)){
							
							loser = playerlist[z-1][m+x+1];
						}else{
							loser = playerlist[z-1][m+x];
							
						}
						ConfigLogUtil.log(ConfigConstants.LOGFILE, "out area");
						if (loser.equals("bye")){
							 x++;
							 continue;
						}
						if (ad4.get(loser)>highest.get(winner))
							highest.put(winner, ad4.get(loser));
						
						lowest.put(loser, ad4.get(winner));
						
					   
						int delta = functionWinAndLoss(ad3, winner, loser);
						
						int worg=ad4.get(winner);
						ad4.put(winner, worg+delta);
						ConfigLogUtil.log(ConfigConstants.LOGFILE, "ad4winner "+ad4.get(winner));
						
						int lorg = ad4.get(loser);
						ad4.put(loser, lorg-delta);
						ConfigLogUtil.log(ConfigConstants.LOGFILE, "ad4loser "+ad4.get(loser));
						x++;
					}
					
				
				}
				p4=p4/2;
			}
		
			//print out result for phase 4
			if ((String)session.getAttribute("leaguedir")!=null && !((String)session.getAttribute("leaguedir")).equals("no")){
				ConfigLogUtil.log(ConfigConstants.LOGFILE, "Checking after phase4"); 
				HashMap<String, Integer> temp4 = new HashMap<String, Integer>(ad4);
				Iterator tmpiter4 = temp4.entrySet().iterator();
				while (tmpiter4.hasNext()) {
					Map.Entry tem4Entry = (Map.Entry) tmpiter4.next();
					int tmp4Rate = (Integer)tem4Entry.getValue();
					String tmp4Key = (String)tem4Entry.getKey();
					String rstat2 = "update temprating set NowRate='"+tmp4Rate+"' where LeagueName='"+leaguename+"' and MatchName='"+matchname+"' and PlayerName='"+tmp4Key+"'";
					executeStmt(rcon, rstat2);
					ConfigLogUtil.log(ConfigConstants.LOGFILE, "iniKey="+tmp4Key);
					ConfigLogUtil.log(ConfigConstants.LOGFILE, "iniRate="+Integer.toString(tmp4Rate));
				//ConfigLogUtil.log(ConfigConstants.LOGFILE, "iniRate="+tmp4Rate); 
				}
				ConfigLogUtil.log(ConfigConstants.LOGFILE, "Checking done after phase4"); 
				rcon.close();
			}
		//end phase4
				
			
			session.setAttribute("finalrates", ad4);
		}
			session.setAttribute("countchecker", countChecker);
			session.setAttribute("playerlist", playerlist);
			session.setAttribute("gamename", matchname);
			session.setAttribute("scores", score);
			session.setAttribute("leaguename", leaguename);
			if (desc != null && !desc.equals(""))
				session.setAttribute("description", desc);
			ConfigLogUtil.log(ConfigConstants.LOGFILE, "I am playoff");
			
			request.getRequestDispatcher("playoffshow.jsp").forward(request, response);
			con.close();
			return;
			
		}
	

		
		//////////////////////////////////////////////////////////////////////////////////////////
	
		String stm0 = "select count(*) from players where MatchName='"+matchname+"'"+" and GroupNumber=1"+" and BracketNumber=1";
		ConfigLogUtil.log(ConfigConstants.LOGFILE, stm0);
		ResultSet rs0 = executeQueryStmt(con, stm0);
		int pos = 0;
		while (rs0.next()){
			
			
			pos = rs0.getInt(1);
		}
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "size of group:"+Integer.toString(ng));
		int braketnum = pos;
		
		
		if (button.equals("Previous")){
			groupnum=groupnum-1;
		}
		
		if(button.equals("Next")){
			groupnum++;
		}
		
		//get win/lose and get place rank from bracket
				String[] WinNum= new String[100];
				String[] PlaceRank = new String[100];
				
				String stmt55 = "select count(*) from gameresults where MatchName='"+matchname+"'"+" and GroupNum="+groupnum;
				ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt55);
				ResultSet rs55 = executeQueryStmt(con, stmt55);
				int bn=0;
				while(rs55.next()){
					bn = rs55.getInt(1);
					
				}
				for (int n=1; n<=bn; n++){
					String stmt66 = "select WinNum, PlaceRank from gameresults where BracketNumber = "+n+" and MatchName='"+matchname+"'"+" and GroupNum="+groupnum;
					ResultSet rs66 = executeQueryStmt(con, stmt66);
					String win="";
					String place="";
					while (rs66.next())	{
						win = rs66.getString(1);
						place = Integer.toString(rs66.getInt(2));
						
						
					}
					ConfigLogUtil.log(ConfigConstants.LOGFILE, win + " " + place);
					WinNum[n]=win;
					PlaceRank[n]=place;
				}
		
		
		//get number of groups from DB
		//get game type from DB
		//get name, bracket number, position and score for this group number
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "I am 111");
		String stm2 = "select PlayerName, Position, BracketNumber, Scores from players where MatchName='"+matchname+"'"+" and GroupNumber="+groupnum;
		ConfigLogUtil.log(ConfigConstants.LOGFILE, stm2);
		ResultSet rs2 = executeQueryStmt(con, stm2);
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "I am 222");
		String[] players = new String[pos];
		String[][] scores = new String[100][100];
		String[][] result = new String[100][100];
		//while(dbresult){
		//players[bracketnum]=name;
		//scores[bracketnum][position] = score;
		while(rs2.next()) {
			String name = rs2.getString(1);
			int position = rs2.getInt(2);
			int bracket = rs2.getInt(3);
			String score = rs2.getString(4);
			ConfigLogUtil.log(ConfigConstants.LOGFILE, "Name:"+name+" Position:"+Integer.toString(position)+" Bracket:"+Integer.toString(bracket)+" Score:"+score);
			players[bracket-1]=name;
			scores[bracket][position]=score;
			//process and check the score
			//decide if result[bracket][position]="W" or "L";
			
		}
	
		//get initial rate
		
		
		if (countChecker == 1){
		
		int[] initRates = new int[100];
		String st0 = "select Position, InitRate from players, leaguemembers, indivitournament where indivitournament.Email in (select MemEmail from leaguemembers) and players.PlayerName=leaguemembers.MemName and players.MatchName='"+matchname+"'"+" and GroupNumber="+groupnum+" and BracketNumber=Position";
		ConfigLogUtil.log(ConfigConstants.LOGFILE, st0);
		ResultSet r0 = executeQueryStmt(con, st0);
		while (r0.next()){
			
			int p = r0.getInt(1);
			int r = r0.getInt(2);
			initRates[p]=r;
			
			
			
		}
		
		int highest=0;
		int lowest=0;
		//phase1
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "I am phase1");
		int[] adjustRates1 = new int[100];
		for (int x =1; x<=braketnum; x++){
			highest=0;
			lowest=0;
			adjustRates1[x]=initRates[x];
			int change = 0;
			int wintemp = 0;
			int losstemp = 0;
			int marker=0;
			for (int y=1; y<=braketnum; y++){
				if ( x==y)
					continue;
				
				
				
				if (scores[x][y].contains("W")||scores[x][y].contains("w")){
					wintemp = initRates[y];
					if (wintemp > highest)
						highest = wintemp;
					
					change=functionWin(initRates, x, y);
					adjustRates1[x]=adjustRates1[x]+change;
					
				}
				
				if (scores[x][y].contains("L")||scores[x][y].contains("l")){
					marker=1;
					losstemp = initRates[y];
					
					
					
					if (losstemp < lowest || lowest==0)
						lowest = losstemp;
					
					
					change=functionLoss(initRates, x, y);
					adjustRates1[x]=adjustRates1[x]-change;
					
					
				}
				
				
				ConfigLogUtil.log(ConfigConstants.LOGFILE, Integer.toString(x)+" change="+Integer.toString(change));
				
				
			}
			
			
			
			if ((adjustRates1[x]-initRates[x]) < 51){
				adjustRates1[x]=initRates[x];
			}
		
			if (adjustRates1[x]-initRates[x] > 75 && marker==1){
				ConfigLogUtil.log(ConfigConstants.LOGFILE, "before adust highest="+Integer.toString(highest)+" lowest="+Integer.toString(lowest)+" adustrate="+Integer.toString(adjustRates1[x]));
				adjustRates1[x]=(highest + lowest)/4 + adjustRates1[x]/2;
			}
			
			if ((adjustRates1[x]-initRates[x])>=51 && (adjustRates1[x]-initRates[x])<= 75){
				adjustRates1[x]=adjustRates1[x];
			}
			
			if (initRates[x]==0)
				adjustRates1[x]=0;
				
		}
		
		//print out result for phase 1
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "Checking after phase1"); 
		for (int mm =1; mm<=braketnum; mm++){
			
			ConfigLogUtil.log(ConfigConstants.LOGFILE, Integer.toString(mm)+"="+Integer.toString(adjustRates1[mm]));
		}
		
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "Checking done after phase1"); 
	//end phase1
		
		//phase 2
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "I am phase2");
		for (int x =1; x<=braketnum; x++){
			if (adjustRates1[x] != 0)
				continue;
			highest=0;
			lowest=0;
			for (int y=1; y<=braketnum; y++){
				if (adjustRates1[y]==0)
					continue;
				
				if (scores[x][y].contains("W")||scores[x][y].contains("w")){
					
					//int winrate=functionUnRateWin(initRates, adjustRates1, x, y);
					int winrate = adjustRates1[y];
					if (winrate > highest)
						highest = winrate;
					
				}
				if (scores[x][y].contains("L")||scores[x][y].contains("l")){
					
					
					int lossrate = adjustRates1[y];  
					if (lossrate < lowest || lowest==0)
						lowest = lossrate;
					
				}
				
				
				
				
				
			}
			
			initRates[x]=(lowest + highest)/2;
		   //calculate unrate based on lowest and highest
			//set into initrate
				
		}
		
		
	    //
	
			
		//print out result for phase 2
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "Checking after phase2"); 
		Connection rcon = getConnectTionFromPool();
		//String drop ="Delete from temprating where LeagueName='"+leaguename+"'";
		//executeStmt(rcon, drop);
		int leng0=0;
		for (int mm =1; mm<=braketnum; mm++){
			if ((String)session.getAttribute("leaguedir")!=null && !((String)session.getAttribute("leaguedir")).equals("no")){
				String rstat = "insert into temprating values ('"+leaguename+"', '"+matchname+"', '"+players[leng0]+"', '"+initRates[mm]+"', 0)";
				executeStmt(rcon, rstat);
			}
			ConfigLogUtil.log(ConfigConstants.LOGFILE, "player="+players[leng0]+" "+Integer.toString(mm)+"="+Integer.toString(initRates[mm]));
			leng0++;
		}
		
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "Checking done after phase2"); 
	//end phase2
		
		//pass3
		
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "I am phase3");
		int[] adjustRates3 = new int[100];
		for (int x =1; x<=braketnum; x++){
			highest=0;
			lowest=0;
			int change=0;
			adjustRates3[x]=initRates[x];
			int marker=0;
			for (int y=1; y<=braketnum; y++){
				if ( x==y)
					continue;
				
				
				
				if (scores[x][y].contains("W")||scores[x][y].contains("w")){
					int wintemp = initRates[y];
					if (wintemp > highest)
						highest = wintemp;
					
					change=functionWin(initRates, x, y);
					adjustRates3[x]=adjustRates3[x]+change;
					
				}
				if(scores[x][y].contains("L")||scores[x][y].contains("l")){
					marker=1;
					
					int losstemp = initRates[y];  
					if (losstemp < lowest || lowest==0)
						lowest = losstemp;
					change=functionLoss(initRates, x, y);
					adjustRates3[x]=adjustRates3[x]-change;
					
				}
				
				
				
				
				
			}
			//this guy has the problem.
			if ((adjustRates3[x]-initRates[x]) < 50){
				adjustRates3[x]=initRates[x];
			}
		
			if (adjustRates3[x]-initRates[x] > 75 && marker==1){
				adjustRates3[x]=(highest + lowest)/4 + adjustRates3[x]/2;
			}
			
			if (initRates[x]==0)
				adjustRates3[x]=0;
				
		}
		
		
		//print out result for phase 3
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "Checking after phase3"); 
		for (int mm =1; mm<=braketnum; mm++){
			
			ConfigLogUtil.log(ConfigConstants.LOGFILE, Integer.toString(mm)+"="+Integer.toString(adjustRates3[mm]));
		}
		
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "Checking done after phase3"); 
	//end phase3
		
		//phase4
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "I am phase4");
		int[] adjustRates4 = new int[100];
		for (int x =1; x<=braketnum; x++){
			highest=0;
			lowest=0;
			int change=0;
			adjustRates4[x]=adjustRates3[x];
			for (int y=1; y<=braketnum; y++){
				if ( x==y)
					continue;
				
				
				
				
				
				if (scores[x][y].contains("W")||scores[x][y].contains("w")){
					int wintemp = adjustRates3[y];
					if (wintemp > highest)
						highest = wintemp;
					
					change=functionWin(adjustRates3, x, y);
					ConfigLogUtil.log(ConfigConstants.LOGFILE, "detail steps: "+Integer.toString(adjustRates4[x]));
					ConfigLogUtil.log(ConfigConstants.LOGFILE, "change: "+Integer.toString(change));
					adjustRates4[x]=adjustRates4[x]+change;
					ConfigLogUtil.log(ConfigConstants.LOGFILE, "detail steps: "+Integer.toString(adjustRates4[x]));
					ConfigLogUtil.log(ConfigConstants.LOGFILE, "change: "+Integer.toString(change));
					
				}
				if(scores[x][y].contains("L")||scores[x][y].contains("l")){
					int losstemp = adjustRates3[y];  
					if (losstemp < lowest || lowest==0)
						lowest = losstemp;
					
					change=functionLoss(adjustRates3, x, y);
					ConfigLogUtil.log(ConfigConstants.LOGFILE, "detail steps: "+Integer.toString(adjustRates4[x]));
					ConfigLogUtil.log(ConfigConstants.LOGFILE, "change: "+Integer.toString(change));
					adjustRates4[x]=adjustRates4[x]-change;
					ConfigLogUtil.log(ConfigConstants.LOGFILE, "detail steps: "+Integer.toString(adjustRates4[x]));
					ConfigLogUtil.log(ConfigConstants.LOGFILE, "change: "+Integer.toString(change));
					
				}
				
				ConfigLogUtil.log(ConfigConstants.LOGFILE, "check steps: "+Integer.toString(adjustRates4[x]));
				
				
				
			}
		
			
			if (adjustRates3[x]==0)
				adjustRates4[x]=0;
				
		}
		
		//print out result for phase 4
		if ((String)session.getAttribute("leaguedir")!=null && !((String)session.getAttribute("leaguedir")).equals("no")){
				ConfigLogUtil.log(ConfigConstants.LOGFILE, "Checking after phase4");
				int leng =0;
				
				for (int mm =1; mm<=braketnum; mm++){
					String rstat2 = "update temprating set NowRate='"+adjustRates4[mm]+"' where LeagueName='"+leaguename+"' and MatchName='"+matchname+"' and PlayerName='"+players[leng]+"'";
					executeStmt(rcon, rstat2);
					ConfigLogUtil.log(ConfigConstants.LOGFILE, "player="+players[leng]+" "+Integer.toString(mm)+"="+Integer.toString(adjustRates4[mm]));
					leng++;
				}
				
				ConfigLogUtil.log(ConfigConstants.LOGFILE, "Checking done after phase4"); 
			    rcon.close();
		}
		//end phase4
		
		//get player name based on bracket
		//get oponent name based on bracket=positon
		//get both initial from the other table
		//calculate int pass1[bracket]
		session.setAttribute("adjustrates", adjustRates4);
		
		//
		
		} //end countchecker if
		session.setAttribute("countchecker", countChecker);
		session.setAttribute("playerlist", players);
		session.setAttribute("WinNum", WinNum);
		session.setAttribute("PlaceRank", PlaceRank);
		session.setAttribute("gamename", matchname);
		session.setAttribute("scores", scores);
		session.setAttribute("gn", groupnum);
		session.setAttribute("ng", ng);
		session.setAttribute("numplayer", braketnum);
		request.setAttribute("leaguename", leaguename);
		if (desc != null && !desc.equals(""))
		session.setAttribute("description", desc);
		ConfigLogUtil.log(ConfigConstants.LOGFILE, desc);
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "I am 5555");
		request.getRequestDispatcher("roundrobinshow.jsp").forward(request, response);
		con.close();
		//or
		//request.getRequestDispatcher("playoffedit.jsp").forward(request, response);
		
	}catch(Exception ce){
		ConfigLogUtil.log(ConfigConstants.LOGFILE, ce.getMessage());
	      ce.printStackTrace();
	      
	}
	
	

	

	
	
	
}



private int functionWinAndLoss(HashMap initRates, String x, String y) {
	int delta=0;
	if (initRates.get(x)==(Integer)0 || initRates.get(y)==(Integer)0)
		return 0;
	if ((Integer)initRates.get(x) >= (Integer)initRates.get(y)){
		if ((Integer)initRates.get(x)-(Integer)initRates.get(y)>=0 && (Integer)initRates.get(x)-(Integer)initRates.get(y)<=12 )
			delta=8;
		if ((Integer)initRates.get(x)-(Integer)initRates.get(y)>=13 && (Integer)initRates.get(x)-(Integer)initRates.get(y)<=37 )
			delta=7;
		if ((Integer)initRates.get(x)-(Integer)initRates.get(y)>=38 && (Integer)initRates.get(x)-(Integer)initRates.get(y)<=62 )
			delta=6;
		if ((Integer)initRates.get(x)-(Integer)initRates.get(y)>=63 && (Integer)initRates.get(x)-(Integer)initRates.get(y)<=87 )
			delta=5;
		if ((Integer)initRates.get(x)-(Integer)initRates.get(y)>=88 && (Integer)initRates.get(x)-(Integer)initRates.get(y)<=112 )
			delta=4;
		if ((Integer)initRates.get(x)-(Integer)initRates.get(y)>=113 && (Integer)initRates.get(x)-(Integer)initRates.get(y)<=137 )
			delta=3;
		if ((Integer)initRates.get(x)-(Integer)initRates.get(y)>=138 && (Integer)initRates.get(x)-(Integer)initRates.get(y)<=162 )
			delta=2;
		if ((Integer)initRates.get(x)-(Integer)initRates.get(y)>=163 && (Integer)initRates.get(x)-(Integer)initRates.get(y)<=187 )
			delta=2;
		if ((Integer)initRates.get(x)-(Integer)initRates.get(y)>=188 && (Integer)initRates.get(x)-(Integer)initRates.get(y)<=212 )
			delta=1;
		if ((Integer)initRates.get(x)-(Integer)initRates.get(y)>=213 && (Integer)initRates.get(x)-(Integer)initRates.get(y)<=237 )
			delta=1;
		if ((Integer)initRates.get(x)-(Integer)initRates.get(y)>=238 )
			delta=0;
	}
	if ((Integer)initRates.get(x) < (Integer)initRates.get(y)){
		if ((Integer)initRates.get(y)-(Integer)initRates.get(x)>=0 && (Integer)initRates.get(y)-(Integer)initRates.get(x)<=12 )
			delta=8;
		if ((Integer)initRates.get(y)-(Integer)initRates.get(x)>=13 && (Integer)initRates.get(y)-(Integer)initRates.get(x)<=37 )
			delta=10;
		if ((Integer)initRates.get(y)-(Integer)initRates.get(x)>=38 && (Integer)initRates.get(y)-(Integer)initRates.get(x)<=62 )
			delta=13;
		if ((Integer)initRates.get(y)-(Integer)initRates.get(x)>=63 && (Integer)initRates.get(y)-(Integer)initRates.get(x)<=87 )
			delta=16;
		if ((Integer)initRates.get(y)-(Integer)initRates.get(x)>=88 && (Integer)initRates.get(y)-(Integer)initRates.get(x)<=112 )
			delta=20;
		if ((Integer)initRates.get(y)-(Integer)initRates.get(x)>=113 && (Integer)initRates.get(y)-(Integer)initRates.get(x)<=137 )
			delta=25;
		if ((Integer)initRates.get(y)-(Integer)initRates.get(x)>=138 && (Integer)initRates.get(y)-(Integer)initRates.get(x)<=162 )
			delta=30;
		if ((Integer)initRates.get(y)-(Integer)initRates.get(x)>=163 && (Integer)initRates.get(y)-(Integer)initRates.get(x)<=187 )
			delta=35;
		if ((Integer)initRates.get(y)-(Integer)initRates.get(x)>=188 && (Integer)initRates.get(y)-(Integer)initRates.get(x)<=212 )
			delta=40;
		if ((Integer)initRates.get(y)-(Integer)initRates.get(x)>=213 && (Integer)initRates.get(y)-(Integer)initRates.get(x)<=237 )
			delta=45;
		if ((Integer)initRates.get(y)-(Integer)initRates.get(x)>=238 )
			delta=50;
	
	}
	return delta;
	
}

private int functionLoss(int[] initRates, int x, int y) {
	
	
	int delta=0;
	if (initRates[x]==0 || initRates[y]==0)
		return 0;
	if (initRates[x] >= initRates[y]){
		if (initRates[x]-initRates[y]>=0 && initRates[x]-initRates[y]<=12 )
			delta=8;
		if (initRates[x]-initRates[y]>=13 && initRates[x]-initRates[y]<=37 )
			delta=10;
		if (initRates[x]-initRates[y]>=38 && initRates[x]-initRates[y]<=62 )
			delta=13;
		if (initRates[x]-initRates[y]>=63 && initRates[x]-initRates[y]<=87 )
			delta=16;
		if (initRates[x]-initRates[y]>=88 && initRates[x]-initRates[y]<=112 )
			delta=20;
		if (initRates[x]-initRates[y]>=113 && initRates[x]-initRates[y]<=137 )
			delta=25;
		if (initRates[x]-initRates[y]>=138 && initRates[x]-initRates[y]<=162 )
			delta=30;
		if (initRates[x]-initRates[y]>=163 && initRates[x]-initRates[y]<=187 )
			delta=35;
		if (initRates[x]-initRates[y]>=188 && initRates[x]-initRates[y]<=212 )
			delta=40;
		if (initRates[x]-initRates[y]>=213 && initRates[x]-initRates[y]<=237 )
			delta=45;
		if (initRates[x]-initRates[y]>=238 )
			delta=50;
	}
	if (initRates[x] < initRates[y]){
		if (initRates[y]-initRates[x]>=0 && initRates[y]-initRates[x]<=12 )
			delta=8;
		if (initRates[y]-initRates[x]>=13 && initRates[y]-initRates[x]<=37 )
			delta=7;
		if (initRates[y]-initRates[x]>=38 && initRates[y]-initRates[x]<=62 )
			delta=6;
		if (initRates[y]-initRates[x]>=63 && initRates[y]-initRates[x]<=87 )
			delta=5;
		if (initRates[y]-initRates[x]>=88 && initRates[y]-initRates[x]<=112 )
			delta=4;
		if (initRates[y]-initRates[x]>=113 && initRates[y]-initRates[x]<=137 )
			delta=3;
		if (initRates[y]-initRates[x]>=138 && initRates[y]-initRates[x]<=162 )
			delta=2;
		if (initRates[y]-initRates[x]>=163 && initRates[y]-initRates[x]<=187 )
			delta=2;
		if (initRates[y]-initRates[x]>=188 && initRates[y]-initRates[x]<=212 )
			delta=1;
		if (initRates[y]-initRates[x]>=213 && initRates[y]-initRates[x]<=237 )
			delta=1;
		if (initRates[y]-initRates[x]>=238 )
			delta=0;
		
		
	}
	
	return delta;
	
}

private int functionWin(int[] initRates, int x, int y) {
	int delta=0;
	if (initRates[x]==0 || initRates[y]==0)
		return 0;
	if (initRates[x] >= initRates[y]){
		if (initRates[x]-initRates[y]>=0 && initRates[x]-initRates[y]<=12 )
			delta=8;
		if (initRates[x]-initRates[y]>=13 && initRates[x]-initRates[y]<=37 )
			delta=7;
		if (initRates[x]-initRates[y]>=38 && initRates[x]-initRates[y]<=62 )
			delta=6;
		if (initRates[x]-initRates[y]>=63 && initRates[x]-initRates[y]<=87 )
			delta=5;
		if (initRates[x]-initRates[y]>=88 && initRates[x]-initRates[y]<=112 )
			delta=4;
		if (initRates[x]-initRates[y]>=113 && initRates[x]-initRates[y]<=137 )
			delta=3;
		if (initRates[x]-initRates[y]>=138 && initRates[x]-initRates[y]<=162 )
			delta=2;
		if (initRates[x]-initRates[y]>=163 && initRates[x]-initRates[y]<=187 )
			delta=2;
		if (initRates[x]-initRates[y]>=188 && initRates[x]-initRates[y]<=212 )
			delta=1;
		if (initRates[x]-initRates[y]>=213 && initRates[x]-initRates[y]<=237 )
			delta=1;
		if (initRates[x]-initRates[y]>=238 )
			delta=0;
	}
	if (initRates[x] < initRates[y]){
		if (initRates[y]-initRates[x]>=0 && initRates[y]-initRates[x]<=12 )
			delta=8;
		if (initRates[y]-initRates[x]>=13 && initRates[y]-initRates[x]<=37 )
			delta=10;
		if (initRates[y]-initRates[x]>=38 && initRates[y]-initRates[x]<=62 )
			delta=13;
		if (initRates[y]-initRates[x]>=63 && initRates[y]-initRates[x]<=87 )
			delta=16;
		if (initRates[y]-initRates[x]>=88 && initRates[y]-initRates[x]<=112 )
			delta=20;
		if (initRates[y]-initRates[x]>=113 && initRates[y]-initRates[x]<=137 )
			delta=25;
		if (initRates[y]-initRates[x]>=138 && initRates[y]-initRates[x]<=162 )
			delta=30;
		if (initRates[y]-initRates[x]>=163 && initRates[y]-initRates[x]<=187 )
			delta=35;
		if (initRates[y]-initRates[x]>=188 && initRates[y]-initRates[x]<=212 )
			delta=40;
		if (initRates[y]-initRates[x]>=213 && initRates[y]-initRates[x]<=237 )
			delta=45;
		if (initRates[y]-initRates[x]>=238 )
			delta=50;
	}
	
	return delta;
	
}

private void doGameEdit(HttpServletRequest request, HttpServletResponse response){
	ConfigLogUtil.log(ConfigConstants.LOGFILE, "get into doGameEdit method");
	try{
		//get group number from previous page.
		int groupnum = Integer.parseInt((String)request.getParameter("gnum"));
		if (groupnum==0)
			groupnum=1;
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "group number:"+Integer.toString(groupnum));
		String button = (String)request.getParameter("submit");
		ConfigLogUtil.log(ConfigConstants.LOGFILE, button);

		String matchname = (String)request.getParameter("gamename");
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "matchname:" + matchname);
		Connection con = getConnectTionFromPool();
		if (button.equals("Delete")){
			String dstm1="delete from games where MatchName='"+matchname+"'";
			String dstm2="delete from gameresults where MatchName='"+matchname+"'";
			String dstm3="delete from players where MatchName='"+matchname+"'";
			String dstm4="delete from roundrobin where MatchName='"+matchname+"'";
			ConfigLogUtil.log(ConfigConstants.LOGFILE, dstm1);
			ConfigLogUtil.log(ConfigConstants.LOGFILE, dstm2);
			ConfigLogUtil.log(ConfigConstants.LOGFILE, dstm3);
			ConfigLogUtil.log(ConfigConstants.LOGFILE, dstm4);
			executeStmt(con, dstm1);
			executeStmt(con, dstm2);
			executeStmt(con, dstm3);
			executeStmt(con, dstm4);
			 con.close();
			String mg="The game is deleted.";
			request.setAttribute("ConfirmMessage", mg);
			request.getRequestDispatcher("confirm.jsp").forward(request, response);
			return;
		}
		
		
		String stm1 ="select NumOfGroup, PlayType, BracketNum from games where MatchName='"+matchname+"'";
		ConfigLogUtil.log(ConfigConstants.LOGFILE, stm1);
		ResultSet rs = executeQueryStmt(con, stm1);
		int ng=0;
		String playtype = "";
		int braket = 0;
		while (rs.next()){
			
			
			ng = rs.getInt(1);
			playtype = rs.getString(2);
			braket = rs.getInt(3);
		}
		if (ng==0){
			 con.close();
			String mg="The match can not be found. Please check your match name and try again.";
			request.setAttribute("ConfirmMessage", mg);
			request.getRequestDispatcher("confirm.jsp").forward(request, response);
			return;
			
		}
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "number of group:"+Integer.toString(ng));
		
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "format:"+playtype);
		if (playtype.equals("Play off")){
			//braketnum = ((Integer)session.getAttribute("BracketNum")).intValue();
			
			String stm03 = "select count(*) from players where MatchName='"+matchname+"'"+" and GroupNumber=1"+" and BracketNumber=1";
			ConfigLogUtil.log(ConfigConstants.LOGFILE, stm03);
			ResultSet rs03 = executeQueryStmt(con, stm03);
			int post = 0;
			while (rs03.next()){
				
				
				post = rs03.getInt(1);
			}
			ConfigLogUtil.log(ConfigConstants.LOGFILE, "size of group:"+Integer.toString(post));
			session.setAttribute("TotalNum", post);
			if (button.equals("Save")){
				
				for(int x=1; x<=braket; x++){
					//String name = (String)request.getParameter("V"+x);;
					for ( int y=1; y<=post; y++){
						if (x!=1){
							String score = (String)request.getParameter("s"+Integer.toString(x)+Integer.toString(y));
							ConfigLogUtil.log(ConfigConstants.LOGFILE, "s"+Integer.toString(x)+Integer.toString(y)+" "+score);
							String poname = (String)request.getParameter("n"+Integer.toString(x)+Integer.toString(y));
							ConfigLogUtil.log(ConfigConstants.LOGFILE, "s"+Integer.toString(x)+Integer.toString(y)+" "+poname);
							String stmt04 = "update players set Scores='"+score+"' where MatchName='"+matchname+"' and Position="+y+" and BracketNumber="+x+" and GroupNumber=1";
							ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt04);
							String stmt05 = "update players set PlayerName='"+poname+"' where MatchName='"+matchname+"' and Position="+y+" and BracketNumber="+x+" and GroupNumber=1";
							ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt05);
							executeStmt(con, stmt04);
							executeStmt(con, stmt05);
						}else{
							String poname = (String)request.getParameter("n"+Integer.toString(x)+Integer.toString(y));
							ConfigLogUtil.log(ConfigConstants.LOGFILE, "s"+Integer.toString(x)+Integer.toString(y)+" "+poname);
							String stmt05 = "update players set PlayerName='"+poname+"' where MatchName='"+matchname+"' and Position="+y+" and BracketNumber="+x+" and GroupNumber=1";
							ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt05);
							executeStmt(con, stmt05);
						}
					}
					
					post=post/2;
					
					
					
				}
				
			 }
			
			String stm02="select PlayerName, Position, BracketNumber, Scores from players where MatchName='"+matchname+"'"+" and GroupNumber=1";
			ConfigLogUtil.log(ConfigConstants.LOGFILE, stm02);
			ResultSet rs02 = executeQueryStmt(con, stm02);
			
			String[][] playerlist = new String[100][100];
			String[][] score = new String[100][100];
			while (rs02.next()){
				String name = rs02.getString(1);
				int position = rs02.getInt(2);
				int bracket = rs02.getInt(3);
				String sc = rs02.getString(4);
				ConfigLogUtil.log(ConfigConstants.LOGFILE, "Name:"+name+" Position:"+Integer.toString(position)+" Bracket:"+Integer.toString(bracket)+" Score:"+sc);
				playerlist[bracket][position]=name;
				score[bracket][position]=sc;
				
				
			}
			session.setAttribute("playerlist", playerlist);
			session.setAttribute("gamename", matchname);
			session.setAttribute("scores", score);
			 con.close();
			
			ConfigLogUtil.log(ConfigConstants.LOGFILE, "I am playoff");
			
			request.getRequestDispatcher("playoffedit.jsp").forward(request, response);
			
			return;
			
		}
		
		
		
		
		
		
		
		
		
		
		
		// if it is a clicking of save button
			//{
		
			//for loop based on bracket{
			//for loop based on position{
			//
			//update set score=s[b][p] db table players based on bracket and position, group number.
			//update set name = v[b]
		
			//}
			//}
		String stm0 = "select count(*) from players where MatchName='"+matchname+"'"+" and GroupNumber=1"+" and BracketNumber=1";
		ConfigLogUtil.log(ConfigConstants.LOGFILE, stm0);
		ResultSet rs0 = executeQueryStmt(con, stm0);
		int pos = 0;
		while (rs0.next()){
			
			
			pos = rs0.getInt(1);
		}
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "size of group:"+Integer.toString(ng));
		int braketnum = pos;
		
		if (button.equals("Save")){
			
			for(int x=1; x<=braketnum; x++){
				String winNum = (String)request.getParameter("W"+Integer.toString(x));
				String pn = (String)request.getParameter("playername"+Integer.toString(x-1));
				int rank = Integer.parseInt(request.getParameter("P"+Integer.toString(x)));
				String stmt99 = "update gameresults set WinNum='"+winNum+"' where MatchName='"+matchname+"' and BracketNumber="+x+" and GroupNum="+groupnum;
				ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt99);
				String stmt100 = "update gameresults set PlaceRank="+rank+" where MatchName='"+matchname+"' and BracketNumber="+x+" and GroupNum="+groupnum;
				ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt100);
				String stmt101 = "update gameresults set Name='"+pn+"' where MatchName='"+matchname+"' and BracketNumber="+x+" and GroupNum="+groupnum;
				ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt101);
				executeStmt(con, stmt99);
				executeStmt(con, stmt100);
				executeStmt(con, stmt101);
				//String name = (String)request.getParameter("V"+x);;
				for ( int y=1; y<=pos; y++){
					
					String score = (String)request.getParameter("s"+Integer.toString(x)+Integer.toString(y));
					ConfigLogUtil.log(ConfigConstants.LOGFILE, "s"+Integer.toString(x)+Integer.toString(y)+" "+score);
					String stmt00 = "update players set Scores='"+score+"' where MatchName='"+matchname+"'" + " and Position="+y+" and BracketNumber="+x+" and GroupNumber="+groupnum;
					ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt00);
					String stmt01 = "update players set PlayerName='"+pn+"' where MatchName='"+matchname+"'" + " and Position="+y+" and BracketNumber="+x+" and GroupNumber="+groupnum;
					ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt00);
					//String stmt11 = "update table players set PlayerName='"+name+"' where Position="+y+" and BraketNumber="+x+" and GroupNumber="+groupnum;
					//ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt11);
					executeStmt(con, stmt00);
					executeStmt(con, stmt01);
					//executeStmt(con, stmt11);
				}
				
				
				
				
				
			}
			
			
				
				
			
		}
		
		
		
		
		
		
		if (button.equals("Previous")){
			groupnum=groupnum-1;
		}
		
		if(button.equals("Next")){
			groupnum++;
		}
		
		
		
		//get win/lose and get place rank from bracket
		String[] WinNum= new String[100];
		String[] PlaceRank = new String[100];
		
		String stmt55 = "select count(*) from gameresults where MatchName='"+matchname+"'"+" and GroupNum="+groupnum;
		ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt55);
		ResultSet rs55 = executeQueryStmt(con, stmt55);
		int bn=0;
		while(rs55.next()){
			bn = rs55.getInt(1);
			
		}
		for (int n=1; n<=bn; n++){
			String stmt66 = "select WinNum, PlaceRank from gameresults where BracketNumber = "+n+" and MatchName='"+matchname+"'"+" and GroupNum="+groupnum;
			ResultSet rs66 = executeQueryStmt(con, stmt66);
			String win="";
			String place="";
			while (rs66.next())	{
				win = rs66.getString(1);
				place = Integer.toString(rs66.getInt(2));
				
				
			}
			ConfigLogUtil.log(ConfigConstants.LOGFILE, win + " " + place);
			WinNum[n]=win;
			PlaceRank[n]=place;
		}
		//get number of groups from DB
		//get game type from DB
		//get name, bracket number, position and score for this group number
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "I am 111");
		String stm2 = "select PlayerName, Position, BracketNumber, Scores from players where MatchName='"+matchname+"'"+" and GroupNumber="+groupnum;
		ConfigLogUtil.log(ConfigConstants.LOGFILE, stm2);
		ResultSet rs2 = executeQueryStmt(con, stm2);
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "I am 222");
		String[] players = new String[pos];
		String[][] scores = new String[100][100];
		//while(dbresult){
		//players[bracketnum]=name;
		//scores[bracketnum][position] = score;
		while(rs2.next()) {
			String name = rs2.getString(1);
			int position = rs2.getInt(2);
			int bracket = rs2.getInt(3);
			String score = rs2.getString(4);
			ConfigLogUtil.log(ConfigConstants.LOGFILE, "Name:"+name+" Position:"+Integer.toString(position)+" Bracket:"+Integer.toString(bracket)+" Score:"+score);
			players[bracket-1]=name;
			scores[bracket][position]=score;
			
		}
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "I am 4444");
		//}
		session.setAttribute("playerlist", players);
		session.setAttribute("WinNum", WinNum);
		session.setAttribute("PlaceRank", PlaceRank);
		session.setAttribute("gamename", matchname);
		session.setAttribute("scores", scores);
		session.setAttribute("gn", groupnum);
		session.setAttribute("ng", ng);
		session.setAttribute("numplayer", braketnum);
		 con.close();
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "I am 5555");
		request.getRequestDispatcher("roundrobinedit.jsp").forward(request, response);
		//or
		//request.getRequestDispatcher("playoffedit.jsp").forward(request, response);
		
	}catch(Exception ce){
		ConfigLogUtil.log(ConfigConstants.LOGFILE, ce.getMessage());
	      ce.printStackTrace();
	}
	
	
	
	
	
}
	//not implementation yet
private void doRoundRobin(HttpServletRequest request, HttpServletResponse response){
	ConfigLogUtil.log(ConfigConstants.LOGFILE, "get into roundrobin method");
	try {
	String groupnum = (String)request.getParameter("group");
	String task = (String)request.getParameter("submit");
	if (task.equals("Save")){
		
		//put the players data into db.
		//two for loops (group num)(position)
		int totalnum = ((Integer)session.getAttribute("TotalNum")).intValue();
		String[] playerlist = (String[])session.getAttribute("PlayerList");
		String[] seedlist = (String[])session.getAttribute("SeedList");
		String[] leftlist = (String[])session.getAttribute("LeftList");
		int numgrp = ((Integer)session.getAttribute("GroupNum")).intValue();
		int numingroup=(totalnum-(totalnum % numgrp))/numgrp;
		int leftnum = totalnum-(numgrp * numingroup);
		int numinplay=(totalnum-seedlist.length-leftlist.length)/numgrp;
		int gn;
		int position;
		String playername;
		String matchname = (String)session.getAttribute("MatchName");
		String tname = (String)session.getAttribute("TN");
		long matchid=System.nanoTime();
		String s = "";
		Connection con = getConnectTionFromPool();
		//update games
		
		
		int bracketnum = 0;
		for (int x=0; x < numgrp; x++){
			int p=1;
			gn = x+1;
			position =p;
			p++;
			playername= seedlist[x];
			//need a loop based on numingroup to fill out one bracket
			if ( x < leftnum)
				numingroup=numingroup+1;
			String ss1 = "insert into gameresults values ("+"'"+matchname+"', "+gn+", "+"'"+playername.trim()+"', "+"' ',"+" 0, "+1+", 'none'"+")";
			ConfigLogUtil.log(ConfigConstants.LOGFILE, ss1);
			executeStmt(con, ss1);
			for (int a=1; a<=numingroup; a++){
				bracketnum=1;
				position = a;
				String score=" ";
				
				s = "insert into players values ("+"'"+matchname+"', "+"'"+matchid+"', "+gn+", "+position+", "+"'"+playername.trim()+"', "+bracketnum+", "+"'"+score+"', '"+tname+"')";
				ConfigLogUtil.log(ConfigConstants.LOGFILE, s);
				executeStmt(con, s);
			}
			if ( x < leftnum){
				position = p;
				p++;
				playername = leftlist[x];
				
				//need a loop based on numingroup to fill out one bracket
				
				String ss2 = "insert into gameresults values ("+"'"+matchname+"', "+gn+", "+"'"+playername.trim()+"', "+"' ',"+" 0, "+2+", 'none'"+")";
				ConfigLogUtil.log(ConfigConstants.LOGFILE, ss2);
				executeStmt(con, ss2);
				for (int a=1; a<=numingroup; a++){
					bracketnum = 2;
					position = a;
					String score=" ";
					
					s = "insert into players values ("+"'"+matchname+"', "+"'"+matchid+"', "+gn+", "+position+", "+"'"+playername.trim()+"', "+bracketnum+", "+"'"+score+"', '"+tname+"')";
					ConfigLogUtil.log(ConfigConstants.LOGFILE, s);
					executeStmt(con, s);
				}
			}
			
			int index=(x * numinplay);
			
			for ( int y=0; y < numinplay ; y++){
				
				position = p;
				p++;
				playername = playerlist[index];
				bracketnum++;
				String ss3 = "insert into gameresults values ("+"'"+matchname+"', "+gn+", "+"'"+playername.trim()+"', "+"' ',"+" 0, "+bracketnum+", 'none'"+")";
				ConfigLogUtil.log(ConfigConstants.LOGFILE, ss3);
				executeStmt(con, ss3);
						//need a loop based on numingroup to fill out one bracket
				for (int a=1; a<=numingroup; a++){
					
					position = a;
					String score=" ";
					s = "insert into players values ("+"'"+matchname+"', "+"'"+matchid+"', "+gn+", "+position+", "+"'"+playername.trim()+"', "+bracketnum+", "+"'"+score+"', '"+tname+"')";
					
					ConfigLogUtil.log(ConfigConstants.LOGFILE, s);
					executeStmt(con, s);
				}
				index++;
							
			}
			
			
			
		}
		String upstm = "update games set Showup='Y' where TournamentName='"+tname+"' and MatchName='"+matchname+"'";
		ConfigLogUtil.log(ConfigConstants.LOGFILE, upstm);
		executeStmt(con, upstm);
		con.close();
		 request.setAttribute("ConfirmMessage", "The game is set successfully.");
		request.getRequestDispatcher("confirm.jsp").forward(request, response);
		return;
	}//end save the data.
	int num=0;
	if (task.equals("Previous"))
		num = (Integer.parseInt(groupnum))-1;
	if (task.equals("Next"))
		num = (Integer.parseInt(groupnum))+1;
	
	request.setAttribute("num", num);
	request.getRequestDispatcher("roundrobin.jsp").forward(request, response);
	}catch(Exception e){
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, "exception in roundrobin");
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, e.getMessage());
	  }
	
}

private void doGameConfig(HttpServletRequest request, HttpServletResponse response){
	ConfigLogUtil.log(ConfigConstants.LOGFILE, "get into doGameConfig method");

	String list = (String)request.getParameter("list");
	String groupnum = (String)request.getParameter("groupnum");
	String gamename = (String)request.getParameter("gamename");
	String gamename0= (String)request.getParameter("gamename0");
	String format = (String)request.getParameter("format");
	String tname = (String)request.getParameter("tname");
	String rcount = (String)request.getParameter("ratecount");
	if (gamename0!=null && !gamename0.equals("")){
		gamename=gamename0;
	}
	
	
	
	
	
	String nofg = (String)request.getParameter("groupnum");
	int cr =0;
	if (rcount!=null){
		cr = 1;
	}
	
	int gn = Integer.parseInt(groupnum);
	
	Connection con = getConnectTionFromPool();
	
	//Check Showup flag if it is "Y";
	String stt="select Showup from games where TournamentName='"+tname+"' and MatchName='"+gamename+"'";
try{
	ResultSet ctt = executeQueryStmt(con, stt);
	String showUp ="";
	while (ctt.next()){
		showUp=ctt.getString(1);
	}
	if (showUp.equals("Y")){
		String mg="The match name '"+gamename+"' has been used already. Please use another name.";
		request.setAttribute("ConfirmMessage", mg);
		request.getRequestDispatcher("confirm.jsp").forward(request, response);
		con.close();
		return;
	}
	
	if (tname==null || tname.equals("") || tname.equals("no")){
		tname="not"+gamename;
		String stmt0 = "insert into games values ('"+gamename+"', "+"'"+format+"', "+gn+", "+0+", "+"'"+tname+"', "+cr+", '"+(String)session.getAttribute("sessionMail")+"', "+"'N')" ;
		//executeStmt(con, stmt0);
		if(!executeStmt(con, stmt0)){
			 con.close();
			  String message11 = "The game name has been used.";
			  request.setAttribute("ConfirmMessage", message11);
			  request.getRequestDispatcher("confirm.jsp").forward(request, response);
			  return;
		  }
	}
	
	session.setAttribute("TN", tname);
	
	ConfigLogUtil.log(ConfigConstants.LOGFILE, list);
	ConfigLogUtil.log(ConfigConstants.LOGFILE, groupnum);
	ConfigLogUtil.log(ConfigConstants.LOGFILE, gamename);
	ConfigLogUtil.log(ConfigConstants.LOGFILE, format);
	
	
	//take the seeds out based on groupnum
	//create the groups
	
	
	String stmt = "update games set PlayType ='"+format+"',NumOfGroup="+gn+",BracketNum=0,RateCount="+cr+",CreatedBy='"+(String)session.getAttribute("sessionMail")+"' where TournamentName='"+tname+"' and MatchName='"+gamename+"'"; 
	//String stmt = "insert into games values ('"+gamename+"', "+"'"+format+"', "+gn+", "+0+", "+"'"+tname+"', "+cr+", '"+(String)session.getAttribute("sessionMail")+"')";
	ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt);
	if(!executeStmt(con, stmt)){
		String mg="The match name '"+gamename+"' has been used already. Please use another name.";
		request.setAttribute("ConfirmMessage", mg);
		request.getRequestDispatcher("confirm.jsp").forward(request, response);
		con.close();
		return;
		
	}
	con.close();
	
	if (format.contains("Round Robin")){
		
		//new stuff
				String cc = list.replaceAll(System.getProperty("line.separator"), ":");
				
				ConfigLogUtil.log(ConfigConstants.LOGFILE, "clean "+cc+"end");
				String[] tt=null;
				tt= cc.split(":");
				ConfigLogUtil.log(ConfigConstants.LOGFILE, list);
				String newList ="";
				for (int w=0; w<tt.length; w++){
					
					if ((!tt[w].equals(""))){
						ConfigLogUtil.log(ConfigConstants.LOGFILE, "check"+tt[w]+"it");
						if (!newList.equals("")){
						newList=newList+":"+tt[w];
						}else{
							newList=tt[w];
						}
						
					}
					
					
					
				}
				
				ConfigLogUtil.log(ConfigConstants.LOGFILE, newList);
				
				
				
				//end
	
	String[] s;
	s= newList.split(":");
	//total players
	int totalnum = s.length;
	//check the list indivitournament if it is a tournament.
	ConfigLogUtil.log(ConfigConstants.LOGFILE, newList);
	
	
	
	
	
	
	
	
	String errormsg="";
	
	int numing = (totalnum-(totalnum % gn))/gn;
	if (numing < 2){
		errormsg = "The number of the players or teams in each group can not be less than 2.";
		session.setAttribute("errormsg", errormsg);
		request.getRequestDispatcher("gameconfig.jsp").forward(request, response);
		return;
		
	}
	
	Connection con3= getConnectTionFromPool();
	String st1="select Playername from indivitournament where Tournament='"+tname+"' and MatchName='"+gamename+"'";
	ResultSet ct = executeQueryStmt(con3, st1);
	Vector v = new Vector(100, 10);
	
	while(ct.next()){
		v.add(ct.getString(1).toUpperCase());
	}
	//int num=0;
	if (cr==1){
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "get in");
		
		
			
			//num++;
			//String flag = ct.getString(1);
			
			for (int q=0; q<totalnum; q++){
				
				
				
				if (v.contains(s[q].toUpperCase())){
					if (!v.contains(s[q].toUpperCase())){
						ConfigLogUtil.log(ConfigConstants.LOGFILE, "get issue");
						errormsg =s[q] +" has not joined the tournament "+tname;
						request.setAttribute("ConfirmMessage", errormsg);
						request.getRequestDispatcher("confirm.jsp").forward(request, response);
						con3.close();
						return;
					}
				}
				
			}
		
				
			
			
		
		/*	if (num!=totalnum){
			errormsg = "Make sure all players are registered and joined in"+tname;
			session.setAttribute("ConfirmMessage", errormsg);
			request.getRequestDispatcher("confirm.jsp").forward(request, response);
			return;
		}*/
	}
	
	
	con3.close();
	
	
	
	
	
	
	
	
	
	
	int leftnum = totalnum-(gn * numing);
	//player list
	String[] a = new String[s.length-gn-leftnum];
	//seeds list
	String[] b = new String[gn];
	//make seed list
	for (int j=0; j<gn; j++){
		b[j]=s[j];
	}
	int n = gn;
	
	
	//make playlist
	for (int i=0; i<(s.length-gn-leftnum); i++){
		
		a[i] = s[n];
		n++;	
	}
	
	//make left list
	

	int m = s.length-leftnum;
	String[] c = new String[leftnum];
	for (int g=0; g<leftnum; g++){
		c[g] = s[m];
		m++;
	}
	
	String total=Integer.toString(totalnum);

	
	//shuffle player list.
	Collections.shuffle(Arrays.asList(a));
	ConfigLogUtil.log(ConfigConstants.LOGFILE, total);
	
	Vector players = new Vector(100, 10);
	
	
	session.setAttribute("Players", players);
	
	session.setAttribute("TotalNum", totalnum);
	session.setAttribute("GroupNum", gn);
	session.setAttribute("PlayerList", a);
	session.setAttribute("SeedList", b);
	session.setAttribute("MatchName", gamename);
	request.setAttribute("num", 1);
	session.setAttribute("LeftNum", leftnum);
	session.setAttribute("LeftList", c);
	//check the reminder is 0 or not.
	//if (totalnum mod gn)
		
		
		
		
		
	request.getRequestDispatcher("roundrobin.jsp").forward(request, response);
	}else{
		
		//new stuff
		String cc = list.replaceAll(System.getProperty("line.separator"), ":");
		
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "clean "+cc+"end");
		String[] tt=null;
		tt= cc.split(":");
		ConfigLogUtil.log(ConfigConstants.LOGFILE, list);
		String newList ="";
		for (int w=0; w<tt.length; w++){
			
			if ((!tt[w].equals(""))){
				ConfigLogUtil.log(ConfigConstants.LOGFILE, "check"+tt[w]+"it");
				if (!newList.equals("")){
				newList=newList+":"+tt[w];
				}else{
					newList=tt[w];
				}
				
			}
			
			
			
		}
		
		ConfigLogUtil.log(ConfigConstants.LOGFILE, newList);
		
		
		
		//end
		String[] s;
		s= newList.split(":");
		//total players
		int totalnum = s.length;
		int num=0;
		//check the list indivitournament if it is a tournament.
		Connection con4= getConnectTionFromPool();
		String st1="select Playername from indivitournament where Tournament='"+tname+"' and MatchName='"+gamename+"'";
		ResultSet ct = executeQueryStmt(con4, st1);
		Vector v = new Vector(100, 10);
		
		while(ct.next()){
			v.add(ct.getString(1).toUpperCase());
		}
		//int num=0;
		if (cr==1){
			ConfigLogUtil.log(ConfigConstants.LOGFILE, "get in "+totalnum);
			
			
				
				//num++;
				//String flag = ct.getString(1);
				
				
				for (int q=0; q<totalnum; q++){
					
					
					ConfigLogUtil.log(ConfigConstants.LOGFILE, "name="+s[q]);
					if (!v.contains(s[q].toUpperCase())){
						ConfigLogUtil.log(ConfigConstants.LOGFILE, "get issue");
						String errormsg =s[q] +" has not joined the tournament "+tname;
						ConfigLogUtil.log(ConfigConstants.LOGFILE, errormsg);
						request.setAttribute("ConfirmMessage", errormsg);
						request.getRequestDispatcher("confirm.jsp").forward(request, response);
						con4.close();
						return;
					}
					
				}
				
					
				
				
			
		
		}
		
		con4.close();
		
		
		
		//if (totalnum%2 != 0)
		//   totalnum = s.length + 1;
		ConfigLogUtil.log(ConfigConstants.LOGFILE, Integer.toString(totalnum));
		int x=0;
		while (Math.pow(2, x) < totalnum){
			x++;
		}
		
		ConfigLogUtil.log(ConfigConstants.LOGFILE, Integer.toString(x));
		int faketotal = (int)Math.pow(2, x);
		
		
		String[] temp = new String[faketotal];
		for (int z=0; z<faketotal; z++){
			if(z < s.length){
				temp[z] = s[z];
			}else{
				temp[z]="bye";
			}
		}
		
		//shaffer to make it fair.
		
				for (int m=0; m<(faketotal/2); m++){
					if ((m % 2)!=0){
						
						if (m < (faketotal/2 - m)){
							String tp = temp[m];
							ConfigLogUtil.log(ConfigConstants.LOGFILE, "from "+tp);
							ConfigLogUtil.log(ConfigConstants.LOGFILE, "to "+ temp[faketotal/2-m]);
							temp[m]=temp[faketotal/2-m];
							temp[faketotal/2-m]=tp;
						}
					}
					
					
				}
				
		//end
		//second part shaffer
				int scounter=0;
				for (int m=faketotal-1 ; m>((faketotal/2)+(faketotal/2)/2-1); m--){
					
					if ((m % 2)==0){
						
						
							String tp = temp[m];
							ConfigLogUtil.log(ConfigConstants.LOGFILE, "from "+tp);
							ConfigLogUtil.log(ConfigConstants.LOGFILE, "to "+ temp[(faketotal/2)+scounter]);
							temp[m]=temp[(faketotal/2)+scounter];
							temp[(faketotal/2)+scounter]=tp;
							scounter=scounter+2;
						
					}
					
					
				}	
				
				
				
				
		//end
		
		String[] f = new String[faketotal];
		
		int counter=1;
		int a = 0;
		
		
		for (int j=0; j<faketotal; j=j+2){
			
			f[j]=temp[a];
			f[j+1]=temp[temp.length-counter];
			
			
			counter++;
			a++;
		}
		String tm=f[faketotal-1];
		f[faketotal-1]=f[faketotal-2];
		f[faketotal-2]=tm;
		
		
		
		
		
		
		session.setAttribute("BracketNum", x);
		Connection con1 = getConnectTionFromPool();
		x++;
		String stmt01 = "update games set BracketNum='"+x+"' where MatchName='"+gamename+"'";
		ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt01);
		executeStmt(con1, stmt01);
		con1.close();
		session.setAttribute("MatchName", gamename);
		session.setAttribute("TotalNum", faketotal);
		session.setAttribute("PlayerList", f);
		request.getRequestDispatcher("playoff.jsp").forward(request, response);
		
	}
	}catch(Exception e){
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, "exception in doGameConfig");
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, e.getMessage());
		  e.printStackTrace();
	  }
	//
	
	
}

private void doPlayOff(HttpServletRequest request, HttpServletResponse response){
	ConfigLogUtil.log(ConfigConstants.LOGFILE, "In the doPlayOff method.");
	try{
	
	String matchname = (String)session.getAttribute("MatchName");
	String tname = (String)session.getAttribute("TN");
	long matchid=System.nanoTime();
	String[] pl = (String[])session.getAttribute("PlayerList");
	Connection con = getConnectTionFromPool();
	int braketnum = ((Integer)session.getAttribute("BracketNum")).intValue();
	int playnum = pl.length;
	int gn =1;
	String playername;
	for (int a=0; a <= braketnum; a++){
		int position = 1;
		for (int b=0; b<playnum; b++){
			if (a==0){
				playername = pl[position-1];
			}else{
				playername = " ";
			}
			int bracket = a+1;
			String score=" ";
			String s = "insert into players values ("+"'"+matchname+"', "+"'"+matchid+"', "+gn+", "+position+", "+"'"+playername.trim()+"', "+bracket+", "+"'"+score+"', '"+tname+"')";
		
			ConfigLogUtil.log(ConfigConstants.LOGFILE, s);
			executeStmt(con, s);
			
			position ++;
		}
		playnum = playnum/2;
		
	}
	String upstm = "update games set Showup='Y' where TournamentName='"+tname+"' and MatchName='"+matchname+"'";  
	executeStmt(con, upstm);
	con.close();
	 request.setAttribute("ConfirmMessage", "The game is set successfully.");
	request.getRequestDispatcher("confirm.jsp").forward(request, response);
	}catch(Exception e){
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, "exception in doPlayOff");
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, e.getMessage());
		  e.printStackTrace();
		
		
		
		
	}
	
	
	
}

private void doContactCoach(HttpServletRequest request, HttpServletResponse response){
	try {
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "get into doContactCoach method");
		boolean rowSelected = false;
		String message = (String)request.getParameter("Message");
		String smail = (String)session.getAttribute("sessionMail");
		
		 for (int i=1; i<=Integer.parseInt((request.getParameter("rowsize"))); i++ ){
			  
			  String email = (String)request.getParameter("name"+i);
			  if (email != null && email !=""){
				  
				 rowSelected = true;
				
				  String event="Looking for a coach";
				  sendMail(message, email, event);
				//  ConfigLogUtil.log(ConfigConstants.LOGFILE, emails[j]);
				  
				 
				  
			  }
			  
			  
		  }
		
		

		
		
		String message5 = ConfigConstants.CONFIRM_MESSAGE2;
		String message4 = ConfigConstants.CONFIRM_MESSAGE3;
		if (!rowSelected){
			request.setAttribute("ConfirmMessage", message5);
		}else{
			request.setAttribute("ConfirmMessage", message4);	
		}
		request.getRequestDispatcher("confirm.jsp").forward(request, response);
	}catch(Exception e){
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, "exception in selectCoach");
		  ConfigLogUtil.log(ConfigConstants.LOGFILE, e.getMessage());
	  }
	//not implementation yet.
}

private void doBackLogin(HttpServletRequest request, HttpServletResponse response){
	
	//
	ConfigLogUtil.log(ConfigConstants.LOGFILE, "I am in doBackLogin");
	Connection con = getConnectTionFromPool();
	ConfigLogUtil.log(ConfigConstants.LOGFILE, "I passed getConnection.");
	//String stmt = "select Pwd from login where Email='mymtl@yahoo.com'";
	String stmt=null;
	String pwd="";
	try{
		String loginName = (String)session.getAttribute("sessionMail");
		if (loginName==null || loginName.equalsIgnoreCase("anonymous@yahoo.com")){
			response.sendRedirect("http://www.pingpongmatch.com");
			return;
		}
		
		//String loginPwd = (String)session.getAttribute("sessionPwd");
		stmt = "select Pwd from login where Email="+"'"+loginName+"'";
		ResultSet rs = executeQueryStmt(con, stmt);
		while (rs.next())
			pwd = rs.getString(1);
		
	
		
		con.close();
		
		if (pwd!=null && pwd!="") {
			//session.setAttribute("pwd", "yes");
			pwdValid=1;
			session.setAttribute("sessionMail", loginName);
			session.setAttribute("sessionPwd", pwd);
			con = getConnectTionFromPool();
			stmt = "select Username, State, Country, Lowrate, Highrate, Locations  from ppmembers where Email="+"'"+loginName+"'";
			rs = executeQueryStmt(con, stmt);
			String username="";
			String state="";
			String nation="";
			String lowrate="";
			String highrate="";
			String location="";
			while (rs.next()){
				username = rs.getString(1);
				state=rs.getString(2);
				nation=rs.getString(3);
				lowrate=rs.getString(4);
				highrate=rs.getString(5);
				location=rs.getString(6);
			}
			
			

			String cMonth;
			 String cYear;
			 String cDay = (String) session.getAttribute("sessionDay");
			 cMonth = (String) session.getAttribute("sessionMonth");
			 
			 cYear = (String) session.getAttribute("sessionYear");
			
			

			 
			  String stmt0="delete from schedules where email='"+(String)session.getAttribute("sessionMail")+"' and date < '" + (String)session.getAttribute("sessionCurrentDate") + "' and frequency like 'one time'";
				executeStmt(con, stmt0);
				 ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt0);
			//let login member knows his events.	
			//String stmt1 = "select distinct person.Id, Location from event, person, ppmembers where event.Id=person.Id and event.Status != 'done' and event.Status != 'cancel' and ppmembers.Email=person.Email and ppmembers.State="+"'"+state+"' "+ "and Time > '"+cDate+"'";
				 String stmt1 = "select distinct person.Id, Location from event, person, ppmembers where event.Id=person.Id and event.Status != 'done' and event.Status != 'cancel' and ppmembers.Email=person.Email and ppmembers.Email="+"'"+loginName+"' "+ "and Time > '"+(String) session.getAttribute("sessionTimeStamp")+"'";	 
			ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt1);
			ResultSet rs1 = executeQueryStmt(con, stmt1);
			String stmt2 = "select Username, Lowrate, Highrate, date, day, time, frequency from ppmembers, schedules where ppmembers.Email=schedules.email and schedules.club="+"'"+location+"'";
			ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt2);
			ResultSet rs2 = executeQueryStmt(con, stmt2);
			//con.close();
			session.setAttribute("tempcon", con);
			session.setAttribute("result", rs1);
			session.setAttribute("schedule", rs2);
			session.setAttribute("sessionLocation", location);
			
			session.setAttribute("sessionState", state);
			session.setAttribute("sessionName", username);
			session.setAttribute("sessionNation", nation);
			session.setAttribute("sessionLow", lowrate);
			session.setAttribute("sessionHigh", highrate);
			ConfigLogUtil.log(ConfigConstants.LOGFILE, state+" "+nation);
			//request.getRequestDispatcher("logintasks.jsp").forward(request, response);
			//request.getRequestDispatcher("setrequirementinfo.jsp").forward(request, response);
			 Connection con2 = getConnectTionFromPool();
			  String lnames = "select LeagueName from leagues";
			  ResultSet leaguenames = executeQueryStmt(con2, lnames);
			 
			  request.setAttribute("LeagueNames", leaguenames);
			  request.setAttribute("con", con2);
			  request.getRequestDispatcher("setrequirementinfo.jsp").forward(request, response);
			  
		}else{
			//session.setAttribute("pwd", "no");
			pwdValid=-1;
			request.getRequestDispatcher("partner.jsp").forward(request, response);
			//request.getRequestDispatcher("status.jsp").forward(request, response);
			//select Username, Status from events a, ppmembers b where a.Email=b.Email and a.Id=30387114987898;
		}
		
		// ("pwd", pwd);
		//request.getRequestDispatcher("test.jsp").forward(request, response);
		//con.close();
	}catch(Exception e){
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "got exceptions in getConnection");
		e.printStackTrace();
	}
	
	//Dealing with the result set to check the password.
}
 

private void doLogin(HttpServletRequest request, HttpServletResponse response){
	
	//if special
   
	
	
	
	
	
	
	
	
	
	
	
	
	//end.
	ConfigLogUtil.log(ConfigConstants.LOGFILE, "I am in doLogin");
	Connection con = getConnectTionFromPool();
	ConfigLogUtil.log(ConfigConstants.LOGFILE, "I passed getConnection.");
	//String stmt = "select Pwd from login where Email='mymtl@yahoo.com'";
	String stmt=null;
	String loginName="";
	String loginPwd="";
	String pwd="";
	try{
		/*String special = (String)request.getParameter("special");
		if (special != null&&special.equals("special")){
			Cookie ssoCookie = ControllerUtils.getCookie(SecurityTools.FORUM_COOKIE_NAME);

	        if (ssoCookie == null) {
	        	loginName="invalid";
	        	loginPwd="invalid";
	            return;
	          
	        }

	        //get the email address and the screen name for this user from the cookie
	        String[] emailAndScreenName = SecurityTools.getInstance().decryptCookieValues(ssoCookie.getValue());

	        if (emailAndScreenName == null) {
	        	loginName="invalid";
	        	loginPwd="invalid";
	        	return;
	           
	        }

	        loginName = emailAndScreenName[0];
	        String st = "select Pwd from login where Email="+"'"+loginName+"'";
	        ResultSet rp = executeQueryStmt(con, st);
	        if (rp.next()){
	        	loginPwd = rp.getString(1);
	        }else{
	        	loginPwd="";
	        }

			
		}else{*/
			loginName = (String)request.getParameter("user");
			loginPwd = (String)request.getParameter("pwd");
		//}
		stmt = "select Pwd from login where Email="+"'"+loginName+"'";
		String chstmt = "select * from leagues where LeagueDirectorMail="+"'"+loginName+"'";
		ConfigLogUtil.log(ConfigConstants.LOGFILE, chstmt);
		ResultSet chrs = executeQueryStmt(con, chstmt);
		if (chrs.next()){
			String leaguename = chrs.getString(1);
			session.setAttribute("leaguename", leaguename);
			session.setAttribute("leaguedir", "yes");
		}else{
			String leaguedir = "no";
			session.setAttribute("leaguedir", leaguedir);
			session.setAttribute("leaguename", "no");
		}
		ResultSet rs = executeQueryStmt(con, stmt);
		while (rs.next())
			pwd = rs.getString(1);
		
	
		
		con.close();
		
		if (pwd!=null && pwd!="" && pwd.equals(loginPwd) ){
			//session.setAttribute("pwd", "yes");
			pwdValid=1;
			session.setAttribute("sessionMail", loginName);
			session.setAttribute("sessionPwd", loginPwd);
			con = getConnectTionFromPool();
			stmt = "select Username, State, Country, Lowrate, Highrate, Locations  from ppmembers where Email="+"'"+loginName+"'";
			rs = executeQueryStmt(con, stmt);
			String username="";
			String state="";
			String nation="";
			String lowrate="";
			String highrate="";
			String location="";
			while (rs.next()){
				username = rs.getString(1);
				state=rs.getString(2);
				nation=rs.getString(3);
				lowrate=rs.getString(4);
				highrate=rs.getString(5);
				location=rs.getString(6);
			}
			
			 String cDay = (String) request.getParameter("currentDay");
			 session.setAttribute("sessionDay", cDay);
			  String cMonth = (String) request.getParameter("currentMonth");
			  session.setAttribute("sessionMonth", cMonth);
			  String cYear = (String) request.getParameter("currentYear");
			  session.setAttribute("sessionYear", cYear);
			  String cDate = cYear+"-"+cMonth+"-"+cDay;
			  String sDate = cMonth+"/"+cDay+"/"+cYear;
			  session.setAttribute("sessionCurrentDate", sDate);
			  session.setAttribute("sessionTimeStamp", cDate);
			  String stmt0="delete from schedules where email='"+(String)session.getAttribute("sessionMail")+"' and date < '" + (String)session.getAttribute("sessionCurrentDate") + "' and frequency like 'one time'";
				executeStmt(con, stmt0);
				 ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt0);
			//let login member knows his events.	
			String stmt1 = "select distinct person.Id, Location from event, person, ppmembers where event.Id=person.Id and event.Status != 'done' and event.Status != 'cancel' and ppmembers.Email=person.Email and ppmembers.Email="+"'"+loginName+"' "+ "and Time > '"+cDate+"'";
			ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt1);
			ResultSet rs1 = executeQueryStmt(con, stmt1);
			String stmt2 = "select Username, Lowrate, Highrate, date, day, time, frequency from ppmembers, schedules where ppmembers.Email=schedules.email and schedules.club="+"'"+location+"'";
			ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt2);
			ResultSet rs2 = executeQueryStmt(con, stmt2);
			//con.close();
			//special handling
			if(loginName.equals("anonymous@yahoo.com")){
				ConfigLogUtil.log(ConfigConstants.LOGFILE, "I am in anonymous section now.");
				username="Anonymous";
				String encryptedData = SecurityTools.getInstance().encryptCookieValues(loginName, username);
				 
				//send the cookie using the predefined cookie name
				//Cookie c = new Cookie("jforumUserId", loginName);
				Cookie c = new Cookie(SecurityTools.FORUM_COOKIE_NAME, encryptedData);
				c.setMaxAge(-1);
				c.setPath("/");
				response.addCookie(c);
				response.sendRedirect("http://www.pingpongmatch.com/jforum");
				return;
			}
			if(loginName.equals("admin@yahoo.com"))
				username="Admin";
			String encryptedData = SecurityTools.getInstance().encryptCookieValues(loginName, username);
			 
			//send the cookie using the predefined cookie name
			//Cookie c = new Cookie("jforumUserId", loginName);
			Cookie c = new Cookie(SecurityTools.FORUM_COOKIE_NAME, encryptedData);
			c.setMaxAge(-1);
			c.setPath("/");
			response.addCookie(c);
			
			
			//done
			
			
			//
			session.setAttribute("tempcon", con);
			session.setAttribute("result", rs1);
			session.setAttribute("schedule", rs2);
			session.setAttribute("sessionLocation", location);
			
			session.setAttribute("sessionState", state);
			session.setAttribute("sessionName", username);
			session.setAttribute("sessionNation", nation);
			session.setAttribute("sessionLow", lowrate);
			session.setAttribute("sessionHigh", highrate);
			ConfigLogUtil.log(ConfigConstants.LOGFILE, state+" "+nation);
			//setrequirementinfo
			//request.getRequestDispatcher("logintasks.jsp").forward(request, response);
			//request.getRequestDispatcher("setrequirementinfo.jsp").forward(request, response);
			 Connection con2 = getConnectTionFromPool();
			  String lnames = "select LeagueName from leagues";
			  ResultSet leaguenames = executeQueryStmt(con2, lnames);
			 
			  request.setAttribute("LeagueNames", leaguenames);
			  request.setAttribute("con", con2);
			  request.getRequestDispatcher("setrequirementinfo.jsp").forward(request, response);
		}else{
			//session.setAttribute("pwd", "no");
			pwdValid=-1;
			request.getRequestDispatcher("partner.jsp").forward(request, response);
			//request.getRequestDispatcher("status.jsp").forward(request, response);
			//select Username, Status from events a, ppmembers b where a.Email=b.Email and a.Id=30387114987898;
		}
		
		// ("pwd", pwd);
		//request.getRequestDispatcher("test.jsp").forward(request, response);
		//con.close();
	}catch(Exception e){
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "got exceptions in getConnection");
		e.printStackTrace();
	}
	
	//Dealing with the result set to check the password.
}

private void doSchedule(HttpServletRequest request, HttpServletResponse response){
	
	//
	ConfigLogUtil.log(ConfigConstants.LOGFILE, "I am in doSchedule");
	Connection con = getConnectTionFromPool();
	ConfigLogUtil.log(ConfigConstants.LOGFILE, "I passed getConnection.");
	
	String stmt=null;
	String stmt0=null;
	
	try{
		
		String date = (String)request.getParameter("testinput");
		String day = " ";
		String time = (String)request.getParameter("Time1");
		String country = (String)request.getParameter("country");
		String state = (String)request.getParameter("state");
		String club = (String)request.getParameter("club");
		String schedule = (String)request.getParameter("schedule");
		String email = (String)session.getAttribute("sessionMail");
		String lowrate = (String)session.getAttribute("sessionLow");
		String highrate = (String)session.getAttribute("sessionHigh");
		String remove = (String)request.getParameter("remove");
		  if (remove!=null && remove.equals("Delete Schedule")){
			  stmt0="delete from schedules where email='"+(String)session.getAttribute("sessionMail")+"' and club ='"+club+"'";
			  executeStmt(con, stmt0);
			  request.setAttribute("ConfirmMessage", "Your schedule has been removed.");
				request.getRequestDispatcher("confirm.jsp").forward(request, response);
			  return;
		  }
		
		if (schedule.equals("one time")){
			
			day="N/A";
			
		}
		if (schedule.equals("weekly")){
			stmt0="delete from schedules where email='"+(String)session.getAttribute("sessionMail")+"' and frequency='weekly'"+" and club='"+club+"'";
			executeStmt(con, stmt0);
			date="N/A";
			if ((String)request.getParameter("mon")!=null)
				day=day + " mon,";
			if ((String)request.getParameter("tue")!=null)
				day=day + " tue,";
			if ((String)request.getParameter("wed")!=null)
				day=day + " wed,";
			if ((String)request.getParameter("thu")!=null)
				day=day + " thu,";
			if ((String)request.getParameter("fri")!=null)
				day=day + " fri,";
			if ((String)request.getParameter("sat")!=null)
				day=day + " sat,";
			if ((String)request.getParameter("sun")!=null)
				day=day + " sun,";
			
		}
		if (schedule.equals("daily")){
			stmt0="delete from schedules where email='"+(String)session.getAttribute("sessionMail")+"' and frequency='daily'"+" and club='"+club+"'";
			executeStmt(con, stmt0);
			day="N/A";
			date="N/A";
			
		}
		stmt = "insert into schedules values ("+"'"+email+"', "+"'"+date+"', "+"'"+day+"', "+"'"+time+"', "+"'"+schedule+"', "+"'"+club+"', "+"'"+state+"', "+"'"+country+"'"+")";
		ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt0);
		ConfigLogUtil.log(ConfigConstants.LOGFILE, stmt);
		executeStmt(con, stmt);
		
		
	
		
		con.close();
		
		String message = ConfigConstants.CONFIRM_MESSAGE4;
		  request.setAttribute("ConfirmMessage", message);
		request.getRequestDispatcher("confirm.jsp").forward(request, response);
		
	}catch(Exception e){
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "got exceptions in doschedule");
		e.printStackTrace();
	}
	
	//Dealing with the result set to check the password.
}

private void doFindPwd(HttpServletRequest request, HttpServletResponse response){
	
	//
	ConfigLogUtil.log(ConfigConstants.LOGFILE, "I am in dofindpwd");
	Connection con = getConnectTionFromPool();
	ConfigLogUtil.log(ConfigConstants.LOGFILE, "I passed getConnection.");
	//String stmt = "select Pwd from login where Email='mymtl@yahoo.com'";
	String stmt=null;
	String pwd="";
	try{
		String loginName = (String)request.getParameter("user");
		
		stmt = "select Pwd from login where Email="+"'"+loginName+"'";
		ResultSet rs = executeQueryStmt(con, stmt);
		while (rs.next())
			pwd = rs.getString(1);
		
	
		
		con.close();
		
		if (pwd!=null && pwd!="" ){
			sendMail("This is the password:"+pwd, loginName, "password found.");
			 String message1 = ConfigConstants.CONFIRM_MESSAGE5;
			  request.setAttribute("ConfirmMessage", message1);
			request.getRequestDispatcher("pwdfound.html").forward(request, response);
		}else{
			 String message2 = ConfigConstants.CONFIRM_MESSAGE6;
			  request.setAttribute("ConfirmMessage", message2);
			request.getRequestDispatcher("pwd.jsp").forward(request, response);
			//request.getRequestDispatcher("status.jsp").forward(request, response);
			//select Username, Status from events a, ppmembers b where a.Email=b.Email and a.Id=30387114987898;
		}
		
		// ("pwd", pwd);
		//request.getRequestDispatcher("test.jsp").forward(request, response);
		//con.close();
	}catch(Exception e){
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "got exceptions in getConnection");
		e.printStackTrace();
	}
	
	//Dealing with the result set to check the password.
}


private Connection getConnectTionFromPool(){
	
	String dbURL= "java:comp/env/jdbc/pingpongdb";
	ConfigLogUtil.log(ConfigConstants.LOGFILE, "get into getConnectTionFromPool method");
	try {
		InitialContext ic = new InitialContext();
		DataSource ds=(DataSource) ic.lookup(dbURL);
		Connection myConn = ds.getConnection();
		return myConn;
	}catch (Exception e){
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "got exceptions in getConnection from JNDI");
		ConfigLogUtil.log(ConfigConstants.LOGFILE, e.getMessage());
		return null;
	}
		
		//myConn.close();
	
	
}

private ResultSet executeQueryStmt(Connection con, String query) {
	
	
	ConfigLogUtil.log(ConfigConstants.LOGFILE, "in executeQueryStmt");
	
		try {
	
		    Statement stmt = con.createStatement();
		
		     ResultSet rs = stmt.executeQuery(query);
		     
		    // con.close();
		     
		     ConfigLogUtil.log(ConfigConstants.LOGFILE, "rs is returned");
		     test = true;
		     
		     return rs;
		
		    
		}
		catch (SQLException e) {
			ConfigLogUtil.log(ConfigConstants.LOGFILE, "in SQLException");
		     e.printStackTrace();
		     test = false;
		 }
		 catch (Exception e) {
			 ConfigLogUtil.log(ConfigConstants.LOGFILE, "in Exception");
		     e.printStackTrace();
		     test = false;
		 }
		
	
		return null;

}

private boolean executeStmt(Connection con, String statement) {
	
	
	ConfigLogUtil.log(ConfigConstants.LOGFILE, "in executeStmt");
	
		try {
	
		    Statement stmt = con.createStatement();
		
		     boolean flag=stmt.execute(statement);
		     
		    // con.close();
		     
		     ConfigLogUtil.log(ConfigConstants.LOGFILE, "flag is returned");
		     test = true;
		     
		     return true;
		
		    
		}
		catch (SQLException e) {
			exceptionMsg = e.getMessage();
			ConfigLogUtil.log(ConfigConstants.LOGFILE, exceptionMsg);
			 
		     e.printStackTrace();
		     test = false;
		     return false;
		 }
		 catch (Exception e) {
			 exceptionMsg = e.getMessage();
			 ConfigLogUtil.log(ConfigConstants.LOGFILE, exceptionMsg);
			 
		     e.printStackTrace();
		     test = false;
		 }
		
	
		return false;

}

private void sendMail(String Text, String s, String event){
	String to = s;
	String from = "tt00pingpong@gmail.com";
	String host = "smtp.gmail.com";
	boolean debug = Boolean.valueOf("true").booleanValue();
	ConfigLogUtil.log(ConfigConstants.LOGFILE, "get into sendMail method");

	// create some properties and get the bye Session
	Properties props = new Properties();
	props.put("mail.smtp.user", "tt00pingpong@gmail.com");
	props.put("mail.smtp.host", host);
	props.put("mail.smtp.port", "465");
	props.put("mail.smtp.auth", true);
	props.put("mail.smtp.starttls.enable", "true");
	
	props.put("mail.smtp.socketFactory.port", "465");

	 
	props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
	
	props.put("mail.smtp.socketFactory.fallback", "false");
	if (debug) props.put("mail.debug", "true");

	Session session = Session.getInstance(props, null);
	session.setDebug(debug);
	
	try {
	    // create a message
	    MimeMessage msg = new MimeMessage(session);
	    msg.setFrom(new InternetAddress(from));
	    
	    
	    InternetAddress[] address = {new InternetAddress(to)};
	   
	    msg.setRecipients(Message.RecipientType.TO, address);
	    msg.setSubject("PingPongMatch Invitation"+" "+event);
	    msg.setSentDate(new Date());
	    // If the desired charset is known, you can use
	    // setText(text, charset)
	    msg.setText(Text);
	    Transport t = session.getTransport("smtp");
	    t.connect(host, "tt00pingpong@gmail.com", "@Yongmei7!");
	    msg.saveChanges();      // don't forget this
	    t.sendMessage(msg, msg.getAllRecipients());
	    t.close();

	    

	    
	    //Transport.send(msg);
	} catch (Exception mex) {
		emailError = "Yes";
	    //System.out.println("\n--Exception handling in msgsendsample.java");
		ConfigLogUtil.log(ConfigConstants.LOGFILE, "\n--Exception handling in msgsendsample.java");
	    mex.printStackTrace();
	   // System.out.println();
	    Exception ex = mex;
	    do {
		if (ex instanceof SendFailedException) {
		    SendFailedException sfex = (SendFailedException)ex;
		    Address[] invalid = sfex.getInvalidAddresses();
		    if (invalid != null) {
			//System.out.println("    ** Invalid Addresses");
		    	ConfigLogUtil.log(ConfigConstants.LOGFILE, "    ** Invalid Addresses");	
			if (invalid != null) {
			    for (int i = 0; i < invalid.length; i++) 
				//System.out.println("         " + invalid[i]);
			    	ConfigLogUtil.log(ConfigConstants.LOGFILE, " "+invalid[i]);		
			}
		    }
		    Address[] validUnsent = sfex.getValidUnsentAddresses();
		    if (validUnsent != null) {
			//System.out.println("    ** ValidUnsent Addresses");
			ConfigLogUtil.log(ConfigConstants.LOGFILE, "ValidUnsent Address");
			if (validUnsent != null) {
			    for (int i = 0; i < validUnsent.length; i++) 
				//System.out.println("         "+validUnsent[i]);
			    ConfigLogUtil.log(ConfigConstants.LOGFILE, " "+validUnsent[i]);
			}
		    }
		    Address[] validSent = sfex.getValidSentAddresses();
		    if (validSent != null) {
			//System.out.println("    ** ValidSent Addresses");
		    	ConfigLogUtil.log(ConfigConstants.LOGFILE, "ValidSent Addresses");
			if (validSent != null) {
			    for (int i = 0; i < validSent.length; i++) 
				//System.out.println("         "+validSent[i]);
			    	ConfigLogUtil.log(ConfigConstants.LOGFILE, " "+validSent[i]);
			}
		    }
		}
		//System.out.println();
		if (ex instanceof MessagingException)
		    ex = ((MessagingException)ex).getNextException();
		else
		    ex = null;
	    } while (ex != null);
	}
	
	
	
	
}



}

