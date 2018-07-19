create table ACCOUNT (
IDAccount text primary key,
ID_Client text,
ID_Contract text,
ID_Source text,
ID_Branch text,
ID_Filial text,
Client_type text,
Number text,
Name text,
Currency text,
Action_Start_Date text,
Action_End_Date_Fact text,
Action_End_Date_Plan text
);

create table ADDRESS (
ID_Client text,
Address_Line text,
ID_Source text,
AdrType text,
PostIndex text,
RegionName text,
RegionSocr text,
RaionName text,
RaionSocr text,
CityName text,
CitySocr text,
SettlName text,
SettlSocr text,
StreetName text,
StreetSocr text,
House text,
Vladenie text,
Korpus text,
Stroenie text,
Flat text
);

create table APPLICATION (
ID_App text primary key,
ID_Client text,
ID_Source text,
Product text,
Currency text,
Loan_Amount_Requested text,
Loan_Term_Requested text,
Interest_Rate_Requested text,
Payment_Requested text,
App_Last_Status text,
Interest_Rate_Approved text,
Payment_Approved text,
Term_Approved text,
Amount_Approved text,
Reject_Reason text,
Last_Status_Date text,
Issue_Date text,
App_Date text,
Approval_Due_Date text,
Branch text,
ID_Contract text
);

create table APPLICATION_HISTORY (
ID_StageApp text primary key,
ID_Application text,
ModifiedOn text,
ModifiedBy text,
StageName text,
StatusName text,
Desision text,
MaxCreditSum text,
MaxTerm text,
StageDatetimeIn text,
StageDatetimeOut text
);

create table CARD (
ID_Card text primary key,
ID_Source text,
IDAccount text,
Is_Blocked text,
Is_Active text,
Is_Main text,
Issue_Date text,
Close_Date text,
Close_Plan_Date text,
PAN text,
Is_overdraft text,
Is_salary text,
CreditLimit text,
CreditLimitRUR text,
Currency text,
Card_type text,
Pay_system text,
Plastic_type text,
Last_transaction_date text
);

create table CLIENT (
ID_Client text primary key,
Surname text,
Name text,
Partronymic text,
Gender text,
Birthdate text,
Age text,
Citizenship text,
BankName text,
Branch text,
Filial text,
Is_Office_Within_City text,
Region_Fact text,
City_Fact text,
Region_Reg text,
City_Reg text,
POMK text,
Phone text,
Email text,
Is_Vip text,
Is_Affilated text,
Is_Resident text,
Is_employee text,
Is_Salary_Client text,
Segment_Income text,
Segment_Inc_Criteria text,
Is_Balcklisted text,
Is_Negative text,
is_communication_refused text,
Is_Dead text,
Product_Penetration text,
Service_Penetration text,
Salary_Org text,
Segment_Product text,
Segment_Activity text
);

create table CLIENT_CONTACT (
ID_Contact text,
ID_Client text,
Phone_Number text,
Phone_Type text,
is_actual text
);

create table CLIENT_DEDUP (
ID_Client text primary key,
ID_Client_SMP text,
ID_Client_IKB text,
ID_Client_MOB text,
ID_Client_N text,
INN text,
FIO text,
Birthdate text,
Passport text
);

create table CLIENT_HISTORY (
ID_Client text,
Update_dt text,
BankName text,
Is_Vip text,
Is_Affilated text,
Is_Resident text,
Is_employee text,
Segment_Income text,
Segment_Inc_Criteria text,
Is_Borrower text,
Is_Salary text,
Is_Depositor text,
POMK text,
Is_Blacklisted text,
Is_Negative text,
is_communication_refused text,
Is_Dead text,
Is_New text,
First_Product text,
Month_On_Book text,
Is_churn text,
Segment_Product text,
Segment_Activity text,
Last_product text
);

create table CLIENT_MSB (
ID_Client text primary key,
Client_Name text,
Client_INN text,
Bank text,
Director_FIO text,
Book_Keeper_FIO text,
Director_ID_Client text,
Book_Keeper_ID_Client text,
Decision_Maker text,
Delinquency text,
Email text,
Client_Type text,
City text,
Branch text,
Org_Phone_Number text,
Is_RKO text,
Is_RKO_Active text,
Is_Cred text,
Is_Cred_Active text,
Is_Depos text,
Is_Depos_Active text,
Is_Grnt text,
Is_Grnt_Active text,
Credit_Limit text,
First_Prod_Issue_Date text,
Month_On_Book text,
Is_DBO text,
Is_DBO_Active_3m text,
RKO_Expences_3m text,
DBO_Expences_3m text,
Floating_Debit_1m text,
Floating_Credit_1m text,
Eom_Balance text,
Product_Segment text
);

create table CLIENT_MSB_HISTORY (
ID_Client text,
Update_dt text,
Client_Name text,
Client_INN text,
Bank text,
Delinquency text,
Is_RKO text,
Is_RKO_Active text,
Is_Cred text,
Is_Cred_Active text,
Is_Depos text,
Is_Depos_Active text,
Is_Grnt text,
Is_Grnt_Active text,
Credit_Limit text,
First_Prod_Issue_Date text,
Month_On_Book text,
Is_DBO text,
Is_DBO_Active_3m text,
RKO_Expences_3m text,
DBO_Expences_3m text,
Floating_Debit_1m text,
Floating_Credit_1m text,
Eom_Balance text,
Product_Segment text
);

create table CLIENT_SUMMARY (
ID_Client text primary key,
First_Product_Start_Date text,
Last_Product_End_Date text,
First_Credit_Start_Date text,
Last_Credit_End_Date text,
First_Debit_Start_Date text,
Last_Debit_End_Date text,
First_Salary_Card_Date text,
Last_Salary_Card_End_Date text,
First_CrCard_Date text,
Last_CrCard_End_Date text,
First_Debit_C_Date text,
Last_Debit_C_End_Date text,
Month_on_book text,
Active_Products_num text,
Active_Deposits_num text,
Active_Mortgage_num text,
Closed_Mortgage_num text,
Active_Credits_num text,
Close_Credits_num text,
Active_Cards_num text,
Active_Cards_Trans_3m text,
Closed_Cards_num text,
Transactions_Onbank_6m_num text,
Transactions_Onbank_6m_amt text,
Salary_count_3m text,
Salary_count_6m text,
DVS_Accounts_num text,
Current_Accounts_num text,
Sms_delivered_12m text,
Outbound_Calls_12m text,
Total_Card_Acc_balance text,
Total_Deposit_Acc_Balance text,
Total_Current_Acc_Balance text,
Total_Loan_Sum text,
Overdue_Loan_Sum text,
Monthly_Payment text,
DTI text,
Total_Cards_Turnover_6m text,
Avg_Salary_6m text,
Avg_salary_3m text,
Last_Application_Income text,
Is_Salary_Cardholder text,
Is_Mir_Cardholder text,
Is_Aeroflot_Carвholder text,
Is_Onbank_User text,
Is_Sms_Inform text,
Is_Other_Products text,
Is_Borrower text,
Is_Salary_Client text,
Is_Debitor text,
Is_Appl_3m text,
Is_Appl_Wo_Cl_Refuse_6m text,
Is_Appl_Refused_12m text,
Is_Arrears_0_3m text,
Is_Closed_Cred_Arrs_30m_plus text,
Active_Cred_Arriars text,
Active_Credits_3m text,
Last_Called_Date text,
Last_Contacted_Date text,
Last_Sms_Sent_Date text,
Last_Sms_Deliver_Date text,
Last_CampaignID text,
Channel_Last text
);

create table COMMUNICATION_REFUSE (
ID_Client text,
ID_Bank text,
FIO text,
Phone text,
Comment text
);

create table COMMUNICATION_RESULT_CALL (
ID_Communication text primary key,
ID_Client text,
CampaignID text,
Client_Phone_Number text,
ID_Reaction text,
Comment text,
Phone_Call_Date text,
DateVisit text,
TimeVisit text,
Branch text,
Is_Contacted text,
Is_Interested text,
ID_Offer text,
ID_Participants text,
Login_eployee text,
Is_outbound text
);

create table COMMUNICATION_RESULT_OTHER (
ID_Communication text primary key,
ID_Client text,
CampaignID text,
Contact_information text,
ID_Reaction text,
Communication_Date text,
Is_Contact text,
ID_Offer text,
ID_Participants text,
Channel text
);

create table CREDIT_CONTRACT (
ID_Contract text primary key,
ID_Client text,
IDAccount text,
Client_type text,
ID_Branch text,
ID_Filial text,
Contract_Number text,
Contract_Amnt text,
Currency text,
ID_Product text,
Start_Date text,
Real_Start_Date text,
End_Date_Plan text,
End_Date_Fact text,
Interest_Rate text,
Is_Open text,
Current_sum text
);

create table CREDIT_HISTORY (
ID_Contract text,
Update_dt text,
Credit_Balance text,
Arrears_Balance text,
Fifo_Deliquency_Days text,
Curr_Month_Payment text,
Credit_Limit text,
Credit_Limit_Dated text,
Is_Active_Contract text,
Max_Deliquency_Days text,
Max_Deliquency_Days_12m text
);

create table Bank (
ID_Bank text primary key,
Code text,
Name text
);

create table Filial (
ID_Filial text primary key,
ID_Bank text,
Name text,
NameShort text,
Is_Primary text,
DateFrom text,
DateTo text
);

create table SOURCE (
ID_Source  text primary key,
ID_Bank text,
Code text,
Name text
);

create table BRANCHES (
ID_Branch text primary key,
ID_Bank text,
Filial_ID text,
Bank_Name text,
Filial_Name text,
Branch_Name text,
City_Name text,
Branch_Format text,
Branch_Type text,
Branch_Status text,
Branch_Name_MSB text,
Branch_Adress text
);

create table DEPOSIT_CONTRACT (
ID_Contract text primary key,
ID_Client text,
IDAccount text,
Client_type text,
ID_Branch text,
ID_Filial text,
Contract_Number text,
Currency text,
ID_Product text,
Start_Date text,
Real_Start_Date text,
End_Date_Plan text,
End_Date_Fact text,
Contract_Extension_Date text,
Account_Number text,
Interest_Rate text,
Balance text,
Is_Allowed_Dep_Replen text,
Min_Balance text,
Is_Auto_Extended text,
Amount_Payed text,
Is_Open text
);

create table DEPOSIT_HISTORY (
ID_Contract text,
Update_dt text,
ID_Source text,
ID_Client text,
Incoming_Amount_1m text,
Outcoming_Amount_1m text,
Interest_Rate text,
Balance text,
ID_Product text,
Is_Open text
);

create table INSURANCE  (
Envelope_ID text primary key,
Envelope_Number text,
ID_Client text,
ID_Source text,
ID_Filial text,
ID_Branch text,
Sold_DT text,
Product text,
Amount text,
Bank_Commiss_Amount text,
Category text,
Company text
);

create table IOM_CAMPAIGNS (
CampaignID text primary key,
ID_Template text,
ID_Correlation text,
Type text,
Status text,
Proposed_Product text,
Channel text,
Short_Name text,
Campaign_Name text,
ID_Offer text,
Start_Date text,
End_Date text,
Change_Timestamp text,
Creator_Userid text,
Is_Regular text,
Is_Child_Campaign text,
Parent_CampaignID text,
Schedule text,
ID_Audience text,
Is_Preapproved text
);

create table IOM_SYSTEM (
ID_Sequence text primary key,
ID_Client text,
Event_Code text,
Event_Timestamp text,
Event_Description text
);

create table NEGATIVE_INFORMATION (
ID_Client text,
ID_Bank text,
FIO text,
Phone text,
Comment text,
Comment_Detailed text,
Date_of_call text,
CampaignID text
);

create table OFFERS (
ID_Offer text primary key,
ID_Client text,
CampaignID text,
Proposed_Product text,
Channel text,
Text text,
Lead_Source text,
Is_Child_Offer text,
ID_Parent_Offer text
);

create table PARTICIPANTS (
ID_Participant text primary key,
ID_Offer text,
ID_Client text,
Client_Full_Name text,
Client_Phone_Number text,
BankName text,
Branch text,
Filial text,
Region_Fact text,
City_Fact text,
Region_Reg text,
City_Reg text,
Segment_Income text,
Segment_Product text,
Segment_Activity text
);

create table PASSPORT (
ID_Client text,
ID_Source text,
DocType text,
DocSeries text,
DocNumber text,
DocVDate text,
DocVydan text,
DocPodr text,
DocEndDate text,
IsActual text
);

create table PREAPPROVED (
ID_Preapproved text primary key,
ID_Offer text,
ID_Client text,
ID_Participant text,
Main_Loan_Sum_now text,
Curr_Month_Payment text,
Salary text,
DTI_max text,
DTI_max_FZ text,
Payment text,
Payment_FZ text,
Payment_max_loan text,
Payment_max_loan_FZ text,
Interest1_sum text,
Interest2_sum text,
Interest3_sum text,
Interest4_sum text,
Interest1_sum_FZ text,
Interest2_sum_FZ text,
Interest3_sum_FZ text,
Interest4_sum_FZ text,
Max_loan_sum text,
Max_loan_sum_FZ text,
Interest text,
Interest_FZ text,
Max_loan_sum_temp text,
Max_loan_sum_temp_FZ text,
CampaignID text,
DateCount text
);

create table PRODUCT (
ID_Product text primary key,
Product_MacroName text,
Client_type text,
ProductName text,
DateStart text,
DateFinish text,
ID_Bank text
);

create table REACTIONS (
ID_Reaction text primary key,
Is_Interested text,
Comment text,
Comment_Details text,
Channel text,
Is_Contact text
);

create table "TRANSACTION" (
ID_Transaction text primary key,
ID_Card text,
ID_Source text,
ID_Client text,
Transaction_Date text,
Processing_Date text,
Type text,
ID_Merchant text,
Status text,
Withdrawal_Amnt text,
Comission_Amnt text,
Withdrawal_Currency text,
Comission_Currency text,
Withdrawal_Rub text,
Comission_Rub text
);

ALTER TABLE ACCOUNT ADD CONSTRAINT ACCOUNT_ID_Client_idx FOREIGN KEY (ID_Client) REFERENCES CLIENT_DEDUP(ID_Client);
ALTER TABLE ACCOUNT ADD CONSTRAINT ACCOUNT_ID_Contract_idx FOREIGN KEY (ID_Contract) REFERENCES CREDIT_CONTRACT(ID_Contract);
ALTER TABLE ACCOUNT ADD CONSTRAINT ACCOUNT_ID_Source_idx FOREIGN KEY (ID_Source) REFERENCES SOURCE(ID_Source);
ALTER TABLE ACCOUNT ADD CONSTRAINT ACCOUNT_ID_Branch_idx FOREIGN KEY (ID_Branch) REFERENCES BRANCHES(ID_Branch);
ALTER TABLE ACCOUNT ADD CONSTRAINT ACCOUNT_ID_Filial_idx FOREIGN KEY (ID_Filial) REFERENCES Filial(ID_Filial);
ALTER TABLE ADDRESS ADD CONSTRAINT ADDRESS_ID_Client_idx FOREIGN KEY (ID_Client) REFERENCES CLIENT_DEDUP(ID_Client);
ALTER TABLE ADDRESS ADD CONSTRAINT ADDRESS_ID_Source_idx FOREIGN KEY (ID_Source) REFERENCES SOURCE(ID_Source);
ALTER TABLE APPLICATION ADD CONSTRAINT APPLICATION_ID_Client_idx FOREIGN KEY (ID_Client) REFERENCES CLIENT_DEDUP(ID_Client);
ALTER TABLE APPLICATION ADD CONSTRAINT APPLICATION_ID_Source_idx FOREIGN KEY (ID_Source) REFERENCES SOURCE(ID_Source);
ALTER TABLE APPLICATION ADD CONSTRAINT APPLICATION_ID_Contract_idx FOREIGN KEY (ID_Contract) REFERENCES CREDIT_CONTRACT(ID_Contract);
ALTER TABLE APPLICATION_HISTORY ADD CONSTRAINT APPLICATION_HISTORY_ID_Application_idx FOREIGN KEY (ID_Application) REFERENCES APPLICATION(ID_App);
ALTER TABLE CARD ADD CONSTRAINT CARD_ID_Source_idx FOREIGN KEY (ID_Source) REFERENCES SOURCE(ID_Source);
ALTER TABLE CARD ADD CONSTRAINT CARD_IDAccount_idx FOREIGN KEY (IDAccount) REFERENCES ACCOUNT(IDAccount);
ALTER TABLE CLIENT ADD CONSTRAINT CLIENT_ID_Client_idx FOREIGN KEY (ID_Client) REFERENCES CLIENT_DEDUP(ID_Client);
ALTER TABLE CLIENT_CONTACT ADD CONSTRAINT CLIENT_CONTACT_ID_Client_idx FOREIGN KEY (ID_Client) REFERENCES CLIENT_DEDUP(ID_Client);
ALTER TABLE CLIENT_HISTORY ADD CONSTRAINT CLIENT_HISTORY_ID_Client_idx FOREIGN KEY (ID_Client) REFERENCES CLIENT_DEDUP(ID_Client);
ALTER TABLE CLIENT_MSB ADD CONSTRAINT CLIENT_MSB_ID_Client_idx FOREIGN KEY (ID_Client) REFERENCES CLIENT_DEDUP(ID_Client);
ALTER TABLE CLIENT_MSB ADD CONSTRAINT CLIENT_MSB_Director_ID_Client_idx FOREIGN KEY (Director_ID_Client) REFERENCES CLIENT(ID_Client);
ALTER TABLE CLIENT_MSB ADD CONSTRAINT CLIENT_MSB_Book_Keeper_ID_Client_idx FOREIGN KEY (Book_Keeper_ID_Client) REFERENCES CLIENT(ID_Client);
ALTER TABLE CLIENT_MSB_HISTORY ADD CONSTRAINT CLIENT_MSB_HISTORY_ID_Client_idx FOREIGN KEY (ID_Client) REFERENCES CLIENT_DEDUP(ID_Client);
ALTER TABLE CLIENT_SUMMARY ADD CONSTRAINT CLIENT_SUMMARY_ID_Client_idx FOREIGN KEY (ID_Client) REFERENCES CLIENT_DEDUP(ID_Client);
ALTER TABLE CLIENT_SUMMARY ADD CONSTRAINT CLIENT_SUMMARY_Last_CampaignID_idx FOREIGN KEY (Last_CampaignID) REFERENCES IOM_CAMPAIGNS(CampaignID);
ALTER TABLE COMMUNICATION_REFUSE ADD CONSTRAINT COMMUNICATION_REFUSE_ID_Client_idx FOREIGN KEY (ID_Client) REFERENCES CLIENT_DEDUP(ID_Client);
ALTER TABLE COMMUNICATION_REFUSE ADD CONSTRAINT COMMUNICATION_REFUSE_ID_Bank_idx FOREIGN KEY (ID_Bank) REFERENCES BANK(ID_Bank);
ALTER TABLE COMMUNICATION_RESULT_CALL ADD CONSTRAINT COMMUNICATION_RESULT_CALL_ID_Client_idx FOREIGN KEY (ID_Client) REFERENCES CLIENT_DEDUP(ID_Client);
ALTER TABLE COMMUNICATION_RESULT_CALL ADD CONSTRAINT COMMUNICATION_RESULT_CALL_CampaignID_idx FOREIGN KEY (CampaignID) REFERENCES IOM_CAMPAIGNS(CampaignID);
ALTER TABLE COMMUNICATION_RESULT_CALL ADD CONSTRAINT COMMUNICATION_RESULT_CALL_ID_Reaction_idx FOREIGN KEY (ID_Reaction) REFERENCES REACTIONS(ID_Reaction);
ALTER TABLE COMMUNICATION_RESULT_CALL ADD CONSTRAINT COMMUNICATION_RESULT_CALL_ID_Offer_idx FOREIGN KEY (ID_Offer) REFERENCES OFFERS(ID_Offer);
ALTER TABLE COMMUNICATION_RESULT_CALL ADD CONSTRAINT COMMUNICATION_RESULT_CALL_ID_Participants_idx FOREIGN KEY (ID_Participants) REFERENCES PARTICIPANTS(ID_Participant);
ALTER TABLE COMMUNICATION_RESULT_OTHER ADD CONSTRAINT COMMUNICATION_RESULT_OTHER_ID_Client_idx FOREIGN KEY (ID_Client) REFERENCES CLIENT_DEDUP(ID_Client);
ALTER TABLE COMMUNICATION_RESULT_OTHER ADD CONSTRAINT COMMUNICATION_RESULT_OTHER_CampaignID_idx FOREIGN KEY (CampaignID) REFERENCES IOM_CAMPAIGNS(CampaignID);
ALTER TABLE COMMUNICATION_RESULT_OTHER ADD CONSTRAINT COMMUNICATION_RESULT_OTHER_ID_Reaction_idx FOREIGN KEY (ID_Reaction) REFERENCES REACTIONS(ID_Reaction);
ALTER TABLE COMMUNICATION_RESULT_OTHER ADD CONSTRAINT COMMUNICATION_RESULT_OTHER_ID_Offer_idx FOREIGN KEY (ID_Offer) REFERENCES OFFERS(ID_Offer);
ALTER TABLE COMMUNICATION_RESULT_OTHER ADD CONSTRAINT COMMUNICATION_RESULT_OTHER_ID_Participants_idx FOREIGN KEY (ID_Participants) REFERENCES PARTICIPANTS(ID_Participant);
ALTER TABLE CREDIT_CONTRACT ADD CONSTRAINT CREDIT_CONTRACT_ID_Client_idx FOREIGN KEY (ID_Client) REFERENCES CLIENT_DEDUP(ID_Client);
ALTER TABLE CREDIT_CONTRACT ADD CONSTRAINT CREDIT_CONTRACT_IDAccount_idx FOREIGN KEY (IDAccount) REFERENCES ACCOUNT(IDAccount);
ALTER TABLE CREDIT_CONTRACT ADD CONSTRAINT CREDIT_CONTRACT_ID_Branch_idx FOREIGN KEY (ID_Branch) REFERENCES BRANCHES(ID_Branch);
ALTER TABLE CREDIT_CONTRACT ADD CONSTRAINT CREDIT_CONTRACT_ID_Filial_idx FOREIGN KEY (ID_Filial) REFERENCES Filial(ID_Filial);
ALTER TABLE CREDIT_CONTRACT ADD CONSTRAINT CREDIT_CONTRACT_ID_Product_idx FOREIGN KEY (ID_Product) REFERENCES PRODUCT(ID_Product);
ALTER TABLE CREDIT_HISTORY ADD CONSTRAINT CREDIT_HISTORY_ID_Contract_idx FOREIGN KEY (ID_Contract) REFERENCES CREDIT_CONTRACT(ID_Contract);
ALTER TABLE Filial ADD CONSTRAINT Filial_ID_Bank_idx FOREIGN KEY (ID_Bank) REFERENCES BANK(ID_Bank);
ALTER TABLE SOURCE ADD CONSTRAINT SOURCE_ID_Bank_idx FOREIGN KEY (ID_Bank) REFERENCES BANK(ID_Bank);
ALTER TABLE BRANCHES ADD CONSTRAINT BRANCHES_ID_Bank_idx FOREIGN KEY (ID_Bank) REFERENCES BANK(ID_Bank);
ALTER TABLE BRANCHES ADD CONSTRAINT BRANCHES_Filial_ID_idx FOREIGN KEY (Filial_ID) REFERENCES Filial(ID_Filial);
ALTER TABLE DEPOSIT_CONTRACT ADD CONSTRAINT DEPOSIT_CONTRACT_ID_Client_idx FOREIGN KEY (ID_Client) REFERENCES CLIENT_DEDUP(ID_Client);
ALTER TABLE DEPOSIT_CONTRACT ADD CONSTRAINT DEPOSIT_CONTRACT_IDAccount_idx FOREIGN KEY (IDAccount) REFERENCES ACCOUNT(IDAccount);
ALTER TABLE DEPOSIT_CONTRACT ADD CONSTRAINT DEPOSIT_CONTRACT_ID_Branch_idx FOREIGN KEY (ID_Branch) REFERENCES BRANCHES(ID_Branch);
ALTER TABLE DEPOSIT_CONTRACT ADD CONSTRAINT DEPOSIT_CONTRACT_ID_Filial_idx FOREIGN KEY (ID_Filial) REFERENCES Filial(ID_Filial);
ALTER TABLE DEPOSIT_CONTRACT ADD CONSTRAINT DEPOSIT_CONTRACT_ID_Product_idx FOREIGN KEY (ID_Product) REFERENCES PRODUCT(ID_Product);
ALTER TABLE DEPOSIT_HISTORY ADD CONSTRAINT DEPOSIT_HISTORY_ID_Contract_idx FOREIGN KEY (ID_Contract) REFERENCES DEPOSIT_CONTRACT(ID_Contract);
ALTER TABLE DEPOSIT_HISTORY ADD CONSTRAINT DEPOSIT_HISTORY_ID_Source_idx FOREIGN KEY (ID_Source) REFERENCES SOURCE(ID_Source);
ALTER TABLE DEPOSIT_HISTORY ADD CONSTRAINT DEPOSIT_HISTORY_ID_Client_idx FOREIGN KEY (ID_Client) REFERENCES CLIENT_DEDUP(ID_Client);
ALTER TABLE DEPOSIT_HISTORY ADD CONSTRAINT DEPOSIT_HISTORY_ID_Product_idx FOREIGN KEY (ID_Product) REFERENCES PRODUCT(ID_Product);
ALTER TABLE INSURANCE  ADD CONSTRAINT INSURANCE_ID_Client_idx FOREIGN KEY (ID_Client) REFERENCES CLIENT_DEDUP(ID_Client);
ALTER TABLE INSURANCE  ADD CONSTRAINT INSURANCE_ID_Source_idx FOREIGN KEY (ID_Source) REFERENCES SOURCE(ID_Source);
ALTER TABLE INSURANCE  ADD CONSTRAINT INSURANCE_ID_Filial_idx FOREIGN KEY (ID_Filial) REFERENCES Filial(ID_Filial);
ALTER TABLE INSURANCE  ADD CONSTRAINT INSURANCE_ID_Branch_idx FOREIGN KEY (ID_Branch) REFERENCES BRANCHES(ID_Branch);
ALTER TABLE IOM_CAMPAIGNS ADD CONSTRAINT IOM_CAMPAIGNS_ID_Offer_idx FOREIGN KEY (ID_Offer) REFERENCES OFFERS(ID_Offer);
ALTER TABLE IOM_CAMPAIGNS ADD CONSTRAINT IOM_CAMPAIGNS_Parent_CampaignID_idx FOREIGN KEY (Parent_CampaignID) REFERENCES IOM_CAMPAIGNS(CampaignID);
ALTER TABLE IOM_SYSTEM ADD CONSTRAINT IOM_SYSTEM_ID_Client_idx FOREIGN KEY (ID_Client) REFERENCES CLIENT_DEDUP(ID_Client);
ALTER TABLE NEGATIVE_INFORMATION ADD CONSTRAINT NEGATIVE_INFORMATION_ID_Client_idx FOREIGN KEY (ID_Client) REFERENCES CLIENT_DEDUP(ID_Client);
ALTER TABLE NEGATIVE_INFORMATION ADD CONSTRAINT NEGATIVE_INFORMATION_ID_Bank_idx FOREIGN KEY (ID_Bank) REFERENCES BANK(ID_Bank);
ALTER TABLE NEGATIVE_INFORMATION ADD CONSTRAINT NEGATIVE_INFORMATION_CampaignID_idx FOREIGN KEY (CampaignID) REFERENCES IOM_CAMPAIGNS(CampaignID);
ALTER TABLE OFFERS ADD CONSTRAINT OFFERS_ID_Client_idx FOREIGN KEY (ID_Client) REFERENCES CLIENT_DEDUP(ID_Client);
ALTER TABLE OFFERS ADD CONSTRAINT OFFERS_CampaignID_idx FOREIGN KEY (CampaignID) REFERENCES IOM_CAMPAIGNS(CampaignID);
ALTER TABLE OFFERS ADD CONSTRAINT OFFERS_ID_Parent_Offer_idx FOREIGN KEY (ID_Parent_Offer) REFERENCES OFFERS(ID_Offer);
ALTER TABLE PARTICIPANTS ADD CONSTRAINT PARTICIPANTS_ID_Offer_idx FOREIGN KEY (ID_Offer) REFERENCES OFFERS(ID_Offer);
ALTER TABLE PARTICIPANTS ADD CONSTRAINT PARTICIPANTS_ID_Client_idx FOREIGN KEY (ID_Client) REFERENCES CLIENT_DEDUP(ID_Client);
ALTER TABLE PASSPORT ADD CONSTRAINT PASSPORT_ID_Client_idx FOREIGN KEY (ID_Client) REFERENCES CLIENT_DEDUP(ID_Client);
ALTER TABLE PASSPORT ADD CONSTRAINT PASSPORT_ID_Source_idx FOREIGN KEY (ID_Source) REFERENCES SOURCE(ID_Source);
ALTER TABLE PREAPPROVED ADD CONSTRAINT PREAPPROVED_ID_Offer_idx FOREIGN KEY (ID_Offer) REFERENCES OFFERS(ID_Offer);
ALTER TABLE PREAPPROVED ADD CONSTRAINT PREAPPROVED_ID_Client_idx FOREIGN KEY (ID_Client) REFERENCES CLIENT_DEDUP(ID_Client);
ALTER TABLE PREAPPROVED ADD CONSTRAINT PREAPPROVED_ID_Participant_idx FOREIGN KEY (ID_Participant) REFERENCES PARTICIPANTS(ID_Participant);
ALTER TABLE PREAPPROVED ADD CONSTRAINT PREAPPROVED_CampaignID_idx FOREIGN KEY (CampaignID) REFERENCES IOM_CAMPAIGNS(CampaignID);
ALTER TABLE PRODUCT ADD CONSTRAINT PRODUCT_ID_Bank_idx FOREIGN KEY (ID_Bank) REFERENCES BANK(ID_Bank);
ALTER TABLE "TRANSACTION" ADD CONSTRAINT TRANSACTION_ID_Card_idx FOREIGN KEY (ID_Card) REFERENCES CARD(ID_Card);
ALTER TABLE "TRANSACTION" ADD CONSTRAINT TRANSACTION_ID_Source_idx FOREIGN KEY (ID_Source) REFERENCES SOURCE(ID_Source);
ALTER TABLE "TRANSACTION" ADD CONSTRAINT TRANSACTION_ID_Client_idx FOREIGN KEY (ID_Client) REFERENCES CLIENT_DEDUP(ID_Client);
