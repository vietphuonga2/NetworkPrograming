/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package olympia;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author H
 */
public class Player {

    private Socket socket;
    private String name;
    private int score;
    private int status;
    private boolean isDeny;
    private boolean isHopestar;
    private List<Quiz> listQuiz;

    public Player(Socket socket, String name, int score, int status, boolean isDeny, boolean isHopestar) {
        this.socket = socket;
        this.name = name;
        this.score = score;
        this.status = status;
        this.isDeny = isDeny;
        this.isHopestar = isHopestar;
        this.listQuiz = new ArrayList<>();
    }

    public boolean isIsHopestar() {
        return isHopestar;
    }

    public void setIsHopestar(boolean isHopestar) {
        this.isHopestar = isHopestar;
    }

    public Player() {
        this.listQuiz = new ArrayList<>();
    }

    public boolean isIsDeny() {
        return isDeny;
    }

    public void setIsDeny(boolean isDeny) {
        this.isDeny = isDeny;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<Quiz> getListQuiz() {
        return listQuiz;
    }

    public void setListQuiz(List<Quiz> listQuiz) {
        this.listQuiz = listQuiz;
    }

}
