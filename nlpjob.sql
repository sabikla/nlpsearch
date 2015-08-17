--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: candidate; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE candidate (
    candidateid integer NOT NULL,
    userid integer,
    name character varying(25),
    email character varying(35),
    mobile character varying(12),
    location text,
    experience integer,
    resume text
);


ALTER TABLE public.candidate OWNER TO postgres;

--
-- Name: candidate_candidateid_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE candidate_candidateid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.candidate_candidateid_seq OWNER TO postgres;

--
-- Name: candidate_candidateid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE candidate_candidateid_seq OWNED BY candidate.candidateid;


--
-- Name: company; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE company (
    companyid integer NOT NULL,
    name text,
    address text,
    email character varying(35),
    mobile character varying(35),
    userid integer
);


ALTER TABLE public.company OWNER TO postgres;

--
-- Name: company_companyid_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE company_companyid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.company_companyid_seq OWNER TO postgres;

--
-- Name: company_companyid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE company_companyid_seq OWNED BY company.companyid;


--
-- Name: feedback; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE feedback (
    feedbackid integer NOT NULL,
    name text,
    email text,
    mobile character varying(12),
    subject text,
    message text
);


ALTER TABLE public.feedback OWNER TO postgres;

--
-- Name: feedback_feedbackid_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE feedback_feedbackid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.feedback_feedbackid_seq OWNER TO postgres;

--
-- Name: feedback_feedbackid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE feedback_feedbackid_seq OWNED BY feedback.feedbackid;


--
-- Name: jobapplication; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE jobapplication (
    applicationid integer NOT NULL,
    userid integer,
    jobid text
);


ALTER TABLE public.jobapplication OWNER TO postgres;

--
-- Name: jobapplication_applicationid_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE jobapplication_applicationid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.jobapplication_applicationid_seq OWNER TO postgres;

--
-- Name: jobapplication_applicationid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE jobapplication_applicationid_seq OWNED BY jobapplication.applicationid;


--
-- Name: joblocation; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE joblocation (
    locationid integer NOT NULL,
    location text,
    jobid integer
);


ALTER TABLE public.joblocation OWNER TO postgres;

--
-- Name: joblocation_locationid_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE joblocation_locationid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.joblocation_locationid_seq OWNER TO postgres;

--
-- Name: joblocation_locationid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE joblocation_locationid_seq OWNED BY joblocation.locationid;


--
-- Name: jobskill; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE jobskill (
    skillid integer NOT NULL,
    skill text,
    jobid integer
);


ALTER TABLE public.jobskill OWNER TO postgres;

--
-- Name: jobskill_skillid_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE jobskill_skillid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.jobskill_skillid_seq OWNER TO postgres;

--
-- Name: jobskill_skillid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE jobskill_skillid_seq OWNED BY jobskill.skillid;


--
-- Name: jobtable; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE jobtable (
    jobid integer NOT NULL,
    post text,
    company text,
    description text,
    salary numeric(10,0),
    experience integer
);


ALTER TABLE public.jobtable OWNER TO postgres;

--
-- Name: jobtable_jobid_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE jobtable_jobid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.jobtable_jobid_seq OWNER TO postgres;

--
-- Name: jobtable_jobid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE jobtable_jobid_seq OWNED BY jobtable.jobid;


--
-- Name: login; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE login (
    userid integer NOT NULL,
    username text,
    password text,
    usertype integer
);


ALTER TABLE public.login OWNER TO postgres;

--
-- Name: login_userid_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE login_userid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.login_userid_seq OWNER TO postgres;

--
-- Name: login_userid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE login_userid_seq OWNED BY login.userid;


--
-- Name: messages; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE messages (
    messageid integer NOT NULL,
    jobid text,
    message text
);


ALTER TABLE public.messages OWNER TO postgres;

--
-- Name: messages_messageid_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE messages_messageid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.messages_messageid_seq OWNER TO postgres;

--
-- Name: messages_messageid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE messages_messageid_seq OWNED BY messages.messageid;


--
-- Name: candidateid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY candidate ALTER COLUMN candidateid SET DEFAULT nextval('candidate_candidateid_seq'::regclass);


--
-- Name: companyid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY company ALTER COLUMN companyid SET DEFAULT nextval('company_companyid_seq'::regclass);


--
-- Name: feedbackid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY feedback ALTER COLUMN feedbackid SET DEFAULT nextval('feedback_feedbackid_seq'::regclass);


--
-- Name: applicationid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY jobapplication ALTER COLUMN applicationid SET DEFAULT nextval('jobapplication_applicationid_seq'::regclass);


--
-- Name: locationid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY joblocation ALTER COLUMN locationid SET DEFAULT nextval('joblocation_locationid_seq'::regclass);


--
-- Name: skillid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY jobskill ALTER COLUMN skillid SET DEFAULT nextval('jobskill_skillid_seq'::regclass);


--
-- Name: jobid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY jobtable ALTER COLUMN jobid SET DEFAULT nextval('jobtable_jobid_seq'::regclass);


--
-- Name: userid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY login ALTER COLUMN userid SET DEFAULT nextval('login_userid_seq'::regclass);


--
-- Name: messageid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY messages ALTER COLUMN messageid SET DEFAULT nextval('messages_messageid_seq'::regclass);


--
-- Data for Name: candidate; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY candidate (candidateid, userid, name, email, mobile, location, experience, resume) FROM stdin;
1	3	Sabik	sabikla@yahoo.com	9856985696	Kerala	0	Swipe20.jpg
2	4	Sabik	sabikla@yahoo.com	9856985696	Kerala	0	9856985696Swipe20.jpg
3	5	asd	asdfg@ggg.com	sdf	Calicut	1	sdfSwipe20.jpg
4	8	Leepa	lee@gmail.com	9874563241	Kannur	0	9874563241samplePos
5	9	Anoop	anoop@gmail.com	6574123658	Kottayam	2	6574123658query
6	10	Newcandi	sample@gmail.com	989898989	zxcfasdd	1	989898989Tweet Return Format
7	11	Sruthi	srut@gmail.com	87946545465	Kerala	2	87946545465samplePos
\.


--
-- Name: candidate_candidateid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('candidate_candidateid_seq', 7, true);


--
-- Data for Name: company; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY company (companyid, name, address, email, mobile, userid) FROM stdin;
1	Hello	World	\N	\N	\N
2	hell	World	\N	\N	\N
3	QBUrst techs	Cochin	\N	\N	\N
4	Wipro 	India\r\n	\N	\N	\N
5	infosys computers.	Trivandrum\r\nKerala	info@infosys.com	9874569856	7
\.


--
-- Name: company_companyid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('company_companyid_seq', 5, true);


--
-- Data for Name: feedback; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY feedback (feedbackid, name, email, mobile, subject, message) FROM stdin;
1	Sabik	sabikla@yahoo.com	9847303932	Test feedback	Sample feedback for Jobber.
2	myuser	Second Feedback	9859654752	Second Feedback	Sample feedback
3	t	t	t	t	tt
4	jh	h	jhjhg	hgj	gjhg
\.


--
-- Name: feedback_feedbackid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('feedback_feedbackid_seq', 4, true);


--
-- Data for Name: jobapplication; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY jobapplication (applicationid, userid, jobid) FROM stdin;
4	3	eccbc87e4b5ce2fe28308fd9f2a7baf3
5	3	c9f0f895fb98ab9159f51fd0297e236d
6	8	d3d9446802a44259755d38e6d163e820
7	9	45c48cce2e2d7fbdea1afc51c7c6ad26
8	10	45c48cce2e2d7fbdea1afc51c7c6ad26
9	11	45c48cce2e2d7fbdea1afc51c7c6ad26
10	3	d3d9446802a44259755d38e6d163e820
\.


--
-- Name: jobapplication_applicationid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('jobapplication_applicationid_seq', 10, true);


--
-- Data for Name: joblocation; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY joblocation (locationid, location, jobid) FROM stdin;
1	westhill calicut	2
3	chennai	3
2	cochin	2
4	trivandrum	3
5	mumbai	7
6	mumbai	8
7	delhi	8
8	chennai	9
9	chennai	10
\.


--
-- Name: joblocation_locationid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('joblocation_locationid_seq', 9, true);


--
-- Data for Name: jobskill; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY jobskill (skillid, skill, jobid) FROM stdin;
2	scala	2
3	python	2
5	ios	3
1	java	2
4	android	3
6	apache cordova	3
7	java	7
8	scala	7
9	python	7
10	ruby on rails	7
11	linux	8
12	apache server	8
13	shell script	8
14	c	9
15	c++	9
16	vb	9
17	selenium	10
\.


--
-- Name: jobskill_skillid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('jobskill_skillid_seq', 17, true);


--
-- Data for Name: jobtable; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY jobtable (jobid, post, company, description, salary, experience) FROM stdin;
9	software engineer	infosys computers.	Software engineer at chennai campus	32000	0
10	qa engineer	infosys computers.	QA engineer at chennai campus	32000	0
3	senior software engineer	qburst technologies pvt ltd.	Some Descriptive data	60000	3
2	software engineer	qburst technologies pvt ltd.	Some Descriptive data	40000	0
7	system architect	qburst technologies pvt ltd.	System architect with 15yrs expenc	120000	12
8	infrastructure manager	wipro it infrastructures ltd.	Technical Infra structure manager	25000	0
\.


--
-- Name: jobtable_jobid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('jobtable_jobid_seq', 10, true);


--
-- Data for Name: login; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY login (userid, username, password, usertype) FROM stdin;
1	qbroot	qbroot	1
3	sabikla@yahoo.com	Hello159	2
4	sabikla@yahoo.com	Hello159	2
5	asdfg@ggg.com	asdfg	2
7	info@infosys.com	963258	3
8	lee@gmail.com	lee	2
9	anoop@gmail.com	anoop	2
10	sample@gmail.com	986532	2
11	srut@gmail.com	srut	2
\.


--
-- Name: login_userid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('login_userid_seq', 11, true);


--
-- Data for Name: messages; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY messages (messageid, jobid, message) FROM stdin;
1	45c48cce2e2d7fbdea1afc51c7c6ad26	Hai all candidates applied for software enginner
\.


--
-- Name: messages_messageid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('messages_messageid_seq', 1, true);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

