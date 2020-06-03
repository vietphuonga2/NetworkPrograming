/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package olympia;

import java.util.List;

/**
 *
 * @author H
 */
public class Room {
    private String name;
    private List<Player> player;
    private int status;
    private List<Quiz> listQuiz10;
    private List<Quiz> listQuiz20;
    private List<Quiz> listQuiz30;

    public List<Quiz> getListQuiz10() {
        return listQuiz10;
    }

    public void setListQuiz10(List<Quiz> listQuiz10) {
        this.listQuiz10 = listQuiz10;
    }

    public List<Quiz> getListQuiz20() {
        return listQuiz20;
    }

    public void setListQuiz20(List<Quiz> listQuiz20) {
        this.listQuiz20 = listQuiz20;
    }

    public List<Quiz> getListQuiz30() {
        return listQuiz30;
    }

    public void setListQuiz30(List<Quiz> listQuiz30) {
        this.listQuiz30 = listQuiz30;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Player> getPlayer() {
        return player;
    }

    public void setPlayer(List<Player> player) {
        this.player = player;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
}
