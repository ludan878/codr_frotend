package com.ludan878merpa443.codr;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

import java.util.Collections;
import java.util.Set;

public class SessionManager {
    /**
     * All methods are quite self explanatory .
     */


    // Init vars

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    // Constructor

    public SessionManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences("AppKey", 0);
        this.editor = sharedPreferences.edit();
        editor.apply();
    }

    // Setter methods


    public void setLogin(boolean login) {
        editor.putBoolean("KEY_LOGIN", login);
        editor.commit();
    }

    public void setImage(String fileName){
        editor.putString("P_PIC_NAME", fileName);
        editor.commit();
    }

    public void setUsername(String username){
        editor.putString("USERNAME",username);
        editor.commit();
    }

    public void setCurrent(String username){
        editor.putString("CURRENT",username);
        editor.commit();
    }

    public void setChat(String username){
        editor.putString("CHAT_USER",username);
        editor.commit();
    }

    public void setFriend(String friend){
        editor.putString("FRIEND",friend);
        editor.commit();
    }

    public void setToken(String token){
        editor.putString("TOKEN", token);
        editor.commit();
    }

    // Set email method
    public void setEmail(String email){
        editor.putString("KEY_EMAIL", email);
        editor.commit();
    }

    public void reset(){
        editor.clear();
        editor.commit();
    }

    // Getter

    public boolean getLogin(){
        return sharedPreferences.getBoolean("KEY_LOGIN", false);
    }
    public String getImage(){ return sharedPreferences.getString("P_PIC_NAME", "kisspng-lab-coats-computer-icons-clothing-labrador-5ac85aed605040.7029058515230799173945.png");}



    // Get username method
    public String getEmail(){
        return sharedPreferences.getString("KEY_EMAIL","");
    }
    public String getCurrent(){
        return sharedPreferences.getString("CURRENT","");
    }
    public String getToken(){
        return sharedPreferences.getString("TOKEN","");
    }
    public String getChat() {
        return sharedPreferences.getString("CHAT_USER","");
    }
    public String getUsername() {
        return sharedPreferences.getString("USERNAME", "");
    }
    public String getFriend() {return sharedPreferences.getString("FRIEND","");}
}
