package com.example.jeon.diary;




import java.io.Serializable;

public class DiaryContent implements Serializable{   // 일기장의 내용을 담을 클래스로써, 어레이리스트를 사용할 것이다.

    String date;
    String title;
    String content;
    String path;
    String recPath;

    public DiaryContent(String date, String title , String path ,String content,String recPath ){

        this.date = date;
        this.title = title;
        this.path = path;
        this.content =content;
        this.recPath = recPath;

    }

}



