package net.oschina.app.v2.service;



interface INoticeService
{ 

   void scheduleNotice();
   void requestNotice();
   void clearNotice(int uid,int type);
}