/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package olympia;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author H
 */
public class QuizModel {

    public List<Quiz> getAllQuiz() {
        Connection conn = null;
        CallableStatement callSt = null;
        List<Quiz> listQuiz = new ArrayList<>();
        try {
            conn = ConnectionDB.openConnection();
            callSt = conn.prepareCall("{call getAllQuiz()}");
            ResultSet rs = callSt.executeQuery();
            while (rs.next()) {
                Quiz quiz = new Quiz();
                quiz.setQuestion(rs.getString("Question"));
                quiz.setAnswer(rs.getString("Answer"));
                quiz.setScore(rs.getInt("Score"));
                quiz.setHopestar(rs.getBoolean("HopeStar"));
                quiz.setTime(rs.getInt("Time"));
                listQuiz.add(quiz);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionDB.closeConnection(conn, callSt);
        }
        return listQuiz;
    }
    public List<Quiz> getQuizByScore(int score) {
        Connection conn = null;
        CallableStatement callSt = null;
        List<Quiz> listQuiz = new ArrayList<>();
        try {
            conn = ConnectionDB.openConnection();
            callSt = conn.prepareCall("{call getQuizByScore(?)}");
            callSt.setInt(1, score);
            ResultSet rs = callSt.executeQuery();
            while (rs.next()) {
                Quiz quiz = new Quiz();
                quiz.setQuestion(rs.getString("Question"));
                quiz.setAnswer(rs.getString("Answer"));
                quiz.setScore(rs.getInt("Score"));
                quiz.setHopestar(rs.getBoolean("HopeStar"));
                quiz.setTime(rs.getInt("Time"));
                listQuiz.add(quiz);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionDB.closeConnection(conn, callSt);
        }
        return listQuiz;
    }
}
