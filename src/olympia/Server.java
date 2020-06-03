/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package olympia;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author H
 */
public class Server {

    private int port;
    public static ArrayList<Socket> listSK;
    public static ArrayList<Room> listRoom;
    public static List<Quiz> listQuiz10;
    public static List<Quiz> listQuiz20;
    public static List<Quiz> listQuiz30;

    public Server(int port) {
        this.port = port;
    }

    private void execute() throws IOException {
        ServerSocket server = new ServerSocket(port);
        System.out.println("Server is listening....");
        while (true) {
            Socket socket = server.accept();
            System.out.println("Received request from client : " + socket);
            Server.listSK.add(socket);
            ReadServer read = new ReadServer(socket);
            read.start();
        }

    }

    public static void main(String[] args) throws IOException {
        Server.listSK = new ArrayList<>();
        Server.listRoom = new ArrayList<>();
        Server.listQuiz10 = new ArrayList<>();
        Server.listQuiz20 = new ArrayList<>();
        Server.listQuiz30 = new ArrayList<>();
        QuizModel quizModel = new QuizModel();
        Server.listQuiz10 = quizModel.getQuizByScore(10);
        Server.listQuiz20 = quizModel.getQuizByScore(20);
        Server.listQuiz30 = quizModel.getQuizByScore(30);

        Server server = new Server(3000);
        server.execute();
    }
}

class ReadServer extends Thread {

    public static int NotInRound = 0;
    public static int InRound = 1;
    public static int FinishRound = 2;
    public static int NotInPlay = 0;
    public static int InPlay = 1;
    public static int FinishPlay = 2;
    private Socket server;

    public ReadServer(Socket server) {
        this.server = server;
    }

    @Override
    public void run() {
        DataInputStream dis = null;
        boolean check;
        try {
            dis = new DataInputStream(server.getInputStream());
            while (true) {
                String sms = dis.readUTF();
                if (sms.startsWith("create_")) {
                    String[] smsCreate = sms.split("_");
                    check = true;
                    for (Room room : Server.listRoom) {
                        System.out.println(room.getName());
                        if (smsCreate[1].equals(room.getName())) {
                            check = false;
                            break;
                        }
                    }
                    if (check) {
                        Room room = new Room();
                        Player player = new Player(server, smsCreate[2], 100, NotInRound, false, false);
                        List<Player> listPlayer = new ArrayList<>();
                        listPlayer.add(player);
                        room.setName(smsCreate[1]);
                        room.setPlayer(listPlayer);
                        room.setStatus(0);
                        room.setListQuiz10(Server.listQuiz10);
                        room.setListQuiz20(Server.listQuiz20);
                        room.setListQuiz30(Server.listQuiz30);
                        Server.listRoom.add(room);
                        DataOutputStream dos = new DataOutputStream(server.getOutputStream());
                        dos.writeUTF("Success");
                        dos.writeUTF("(RoomName)" + room.getName());
                        dos.writeUTF("(Welcome)Welcome to the room ! , " + player.getName() + "\n");
                        dos.writeUTF("(Player1)" + player.getName());
                        dos.writeUTF("(Player2)");
                        dos.writeUTF("(Player3)");
                        System.out.println("Client create room successfully: " + room.getName() + " " + player.getSocket().getPort() + " " + player.getName());
                    } else {
                        DataOutputStream dos = new DataOutputStream(server.getOutputStream());
                        dos.writeUTF("(Create)Fail");
                        System.out.println("Client create room fail !");
                    }
                }
                if (sms.startsWith("join_")) {
                    String[] smsCreate = sms.split("_");
                    check = false;
                    for (Room room : Server.listRoom) {
                        if (smsCreate[1].equals(room.getName()) && room.getStatus() == 0 && room.getPlayer().size() < 3) {
                            Player player = new Player(server, smsCreate[2], 100, NotInRound, false, false);
                            room.getPlayer().add(player);
                            DataOutputStream dos = new DataOutputStream(server.getOutputStream());
                            dos.writeUTF("Success");
                            dos.writeUTF("(Welcome)Welcome to the room ! , " + player.getName() + "\n");
                            dos.writeUTF("(RoomName)" + room.getName());
                            for (Player item : room.getPlayer()) {
                                if (item.getSocket().getPort() != server.getPort()) {
                                    dos = new DataOutputStream(item.getSocket().getOutputStream());
                                    dos.writeUTF("(Send)" + player.getName() + " has join the room\n");
                                }
                                dos = new DataOutputStream(item.getSocket().getOutputStream());
                                for (int i = 0; i < 3; i++) {
                                    if (i < room.getPlayer().size()) {
                                        dos.writeUTF("(Player" + (i + 1) + ")" + room.getPlayer().get(i).getName());
                                    } else {
                                        dos.writeUTF("(Player" + (i + 1) + ")");
                                    }
                                }
                            }
                            System.out.println("Client join room successfully: " + room.getName() + " " + player.getSocket().getPort() + " " + player.getName());
                            check = true;
                            break;
                        }
                    }
                    if (check == false) {
                        DataOutputStream dos = new DataOutputStream(server.getOutputStream());
                        dos.writeUTF("(Join)Fail");
                        System.out.println("Client join room fail !");
                    }
                }
                if (sms.startsWith("list")) {
                    String smsList = "(List)";
                    for (int i = 0; i < Server.listRoom.size(); i++) {
                        smsList += Server.listRoom.get(i).getName();
                        if (i != (Server.listRoom.size() - 1)) {
                            smsList += " , ";
                        }
                    }
                    DataOutputStream dos = new DataOutputStream(server.getOutputStream());
                    dos.writeUTF(smsList + "\n");
                }
                if (sms.startsWith("leave")) {
                    for (Room room : Server.listRoom) {
                        for (Player item : room.getPlayer()) {
                            if (item.getSocket().getPort() == server.getPort()) {
                                DataOutputStream dos = new DataOutputStream(server.getOutputStream());
                                dos.writeUTF("leave");
                                room.getPlayer().remove(item);
                                for (Player item1 : room.getPlayer()) {
                                    dos = new DataOutputStream(item1.getSocket().getOutputStream());
                                    dos.writeUTF("(Send)" + item.getName() + " has left the room\n");
                                    for (int i = 0; i < 3; i++) {
                                        if (i < room.getPlayer().size()) {
                                            dos.writeUTF("(Player" + (i + 1) + ")" + room.getPlayer().get(i).getName());
                                        } else {
                                            dos.writeUTF("(Player" + (i + 1) + ")");
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                }

                if (sms.startsWith("(send)")) {
                    for (Room room : Server.listRoom) {
                        for (Player player : room.getPlayer()) {
                            if (player.getSocket().getPort() == server.getPort()) {
                                for (Player player1 : room.getPlayer()) {
                                    DataOutputStream dos = new DataOutputStream(player1.getSocket().getOutputStream());
                                    dos.writeUTF("(Send)" + player.getName() + " : " + sms.substring(6) + "\n");
                                    System.out.println("send message success:" + player.getName() + " , " + sms.substring(6));
                                }
                            }
                        }
                    }

                }
                if (sms.startsWith("start")) {
                    check = false;
                    for (Room room : Server.listRoom) {
                        for (Player player : room.getPlayer()) {
                            if (player.getSocket().getPort() == server.getPort() && room.getPlayer().size() == 3) {
                                room.setStatus(1);
                                check = true;
                                String score = "(scoreboard)";
                                String turn = "(turn)" + room.getPlayer().get(0).getName();
                                String round = "(round)" + room.getPlayer().get(0).getName();
                                room.getPlayer().get(0).setStatus(InRound);
                                DataOutputStream dos;
                                for (Player player1 : room.getPlayer()) {
                                    dos = new DataOutputStream(player1.getSocket().getOutputStream());
                                    dos.writeUTF("start");
                                    score += player1.getName() + " : " + player1.getScore() + "_";
                                    System.out.println("start game success:" + player1.getName() + " , " + room.getName());
                                }
                                for (Player item : room.getPlayer()) {
                                    dos = new DataOutputStream(item.getSocket().getOutputStream());
                                    dos.writeUTF(score);
                                    dos.writeUTF(turn);
                                    dos.writeUTF(round);
                                }
                                dos = new DataOutputStream(room.getPlayer().get(0).getSocket().getOutputStream());
                                dos.writeUTF("(package)");
                                break;
                            }
                        }
                    }
                    if (check == false) {
                        DataOutputStream dos = new DataOutputStream(server.getOutputStream());
                        dos.writeUTF("(Send)Start room fail");
                        System.out.println("Client start room fail !");
                    }
                }
                if (sms.startsWith("(package)")) {
                    DataOutputStream dos;
                    Random generator = new Random();
                    dos = new DataOutputStream(server.getOutputStream());
                    dos.writeUTF("done");
                    System.out.println("Send message done ");
                    for (Room room : Server.listRoom) {
                        for (Player player : room.getPlayer()) {
                            if (player.getSocket().getPort() == server.getPort() && room.getStatus() == 1) {
                                int random;
                                switch (sms.substring(9)) {
                                    case "60 points":
                                        random = generator.nextInt(room.getListQuiz10().size() - 1);
                                        player.getListQuiz().add(room.getListQuiz10().get(random));
                                        room.getListQuiz10().remove(random);
                                        random = generator.nextInt(room.getListQuiz10().size() - 1);
                                        player.getListQuiz().add(room.getListQuiz10().get(random));
                                        room.getListQuiz10().remove(random);
                                        random = generator.nextInt(room.getListQuiz20().size() - 1);
                                        player.getListQuiz().add(room.getListQuiz20().get(random));
                                        room.getListQuiz20().remove(random);
                                        random = generator.nextInt(room.getListQuiz20().size() - 1);
                                        player.getListQuiz().add(room.getListQuiz20().get(random));
                                        room.getListQuiz20().remove(random);
                                        break;
                                    case "80 points":
                                        random = generator.nextInt(room.getListQuiz10().size() - 1);
                                        player.getListQuiz().add(room.getListQuiz10().get(random));
                                        room.getListQuiz10().remove(random);
                                        random = generator.nextInt(room.getListQuiz20().size() - 1);
                                        player.getListQuiz().add(room.getListQuiz20().get(random));
                                        room.getListQuiz20().remove(random);
                                        random = generator.nextInt(room.getListQuiz20().size() - 1);
                                        player.getListQuiz().add(room.getListQuiz20().get(random));
                                        room.getListQuiz20().remove(random);
                                        random = generator.nextInt(room.getListQuiz30().size() - 1);
                                        player.getListQuiz().add(room.getListQuiz30().get(random));
                                        room.getListQuiz30().remove(random);
                                        break;
                                    case "100 points":
                                        random = generator.nextInt(room.getListQuiz20().size() - 1);
                                        player.getListQuiz().add(room.getListQuiz20().get(random));
                                        room.getListQuiz20().remove(random);
                                        random = generator.nextInt(room.getListQuiz20().size() - 1);
                                        player.getListQuiz().add(room.getListQuiz20().get(random));
                                        room.getListQuiz20().remove(random);
                                        random = generator.nextInt(room.getListQuiz30().size() - 1);
                                        player.getListQuiz().add(room.getListQuiz30().get(random));
                                        room.getListQuiz30().remove(random);
                                        random = generator.nextInt(room.getListQuiz30().size() - 1);
                                        player.getListQuiz().add(room.getListQuiz30().get(random));
                                        room.getListQuiz30().remove(random);
                                        break;
                                }
                                for (Player item : room.getPlayer()) {
                                    dos = new DataOutputStream(item.getSocket().getOutputStream());
                                    int quiznum = 5 - player.getListQuiz().size();
                                    dos.writeUTF("(PackagePoint)" + sms.substring(9));
                                    dos.writeUTF("(QuizNumber)Question " + " " + quiznum + ":");
                                    dos.writeUTF("(QuizPoint)" + player.getListQuiz().get(0).getScore() + " points");
                                }
                                dos = new DataOutputStream(server.getOutputStream());
                                dos.writeUTF("(HopeStarOption)");

                                break;
                            }
                        }
                    }
                }
                if (sms.startsWith("(HopeStar)")) {
                    DataOutputStream dos;
                    for (Room room : Server.listRoom) {
                        for (Player player : room.getPlayer()) {
                            if (player.getSocket().getPort() == server.getPort() && room.getStatus() == 1) {
                                switch (sms.substring(10)) {
                                    case "Yes":
                                        player.getListQuiz().get(0).setHopestar(true);
                                        player.setIsHopestar(true);
                                        break;
                                }
                                dos = new DataOutputStream(player.getSocket().getOutputStream());
                                dos.writeUTF("(AnswerButton)");
                                for (Player item : room.getPlayer()) {
                                    dos = new DataOutputStream(item.getSocket().getOutputStream());
                                    dos.writeUTF(sms);
                                    dos.writeUTF("(QuizQuestion)" + player.getListQuiz().get(0).getQuestion());
                                }
                                break;
                            }
                        }
                    }
                }
                if (sms.startsWith("(Answer)")) {
                    DataOutputStream dos;
                    for (Room room : Server.listRoom) {
                        for (Player player : room.getPlayer()) {
                            if (player.getSocket().getPort() == server.getPort() && room.getStatus() == 1) {
                                Player playerInRound = new Player();
                                Player playerInTurn = new Player();
                                for (Player item : room.getPlayer()) {
                                    if (item.getStatus() == InRound) {
                                        playerInRound = item;
                                        break;
                                    }
                                }
                                for (Player item : room.getPlayer()) {
                                    if (item.getSocket().getPort() == server.getPort()) {
                                        playerInTurn = item;
                                        break;
                                    }
                                }
                                int playerInRoundIndex = room.getPlayer().indexOf(playerInRound);
                                int playerInTurnIndex = room.getPlayer().indexOf(playerInTurn);
                                String answer = sms.substring(8).trim();
                                String correctAnswer = playerInRound.getListQuiz().get(0).getAnswer().trim();
                                boolean hopestar = playerInRound.getListQuiz().get(0).isHopestar();
                                int playerInTurnScore = playerInTurn.getScore();
                                int playerInRoundScore = playerInRound.getScore();
                                int questionScore = playerInRound.getListQuiz().get(0).getScore();
                                String score = "(scoreboard)";
                                String turn;
                                if (answer.toLowerCase().equals(correctAnswer.toLowerCase())) {
                                    if (hopestar) {
                                        if (playerInTurn.equals(playerInRound)) {
                                            room.getPlayer().get(playerInTurnIndex).setScore(playerInRoundScore + questionScore * 2);
                                        } else {
                                            dos = new DataOutputStream(playerInTurn.getSocket().getOutputStream());
                                            dos.writeUTF("(RemoveAnswerButton)");
                                            room.getPlayer().get(playerInTurnIndex).setScore(playerInTurnScore + questionScore);
                                        }
                                    } else {
                                        if (playerInTurn.equals(playerInRound)) {
                                            room.getPlayer().get(playerInTurnIndex).setScore(playerInRoundScore + questionScore);
                                        } else {
                                            dos = new DataOutputStream(playerInTurn.getSocket().getOutputStream());
                                            dos.writeUTF("(RemoveAnswerButton)");
                                            room.getPlayer().get(playerInTurnIndex).setScore(playerInTurnScore + questionScore);
                                            room.getPlayer().get(playerInRoundIndex).setScore(playerInRoundScore - questionScore);
                                        }
                                    }
                                    for (Player item : room.getPlayer()) {
                                        score += item.getName() + " : " + item.getScore() + "_";
                                    }
                                    for (Player item : room.getPlayer()) {
                                        dos = new DataOutputStream(item.getSocket().getOutputStream());
                                        dos.writeUTF(score);
                                    }
                                    room.getPlayer().get(playerInRoundIndex).getListQuiz().remove(0);
                                    for (Player item : room.getPlayer()) {
                                        dos = new DataOutputStream(item.getSocket().getOutputStream());
                                        dos.writeUTF("(LastAnswer)" + answer + " (Correct)");
                                        dos.writeUTF("(CorrectAnswer)" + correctAnswer);
                                        dos.writeUTF("(HopeStar)No");
                                    }
                                    if (playerInRound.getListQuiz().size() > 0) {
                                        for (Player item : room.getPlayer()) {
                                            int quiznum = 5 - playerInRound.getListQuiz().size();
                                            turn = "(turn)" + playerInRound.getName();
                                            dos = new DataOutputStream(item.getSocket().getOutputStream());
                                            dos.writeUTF("(QuizNumber)Question " + quiznum + ":");
                                            dos.writeUTF("(QuizPoint)" + playerInRound.getListQuiz().get(0).getScore() + " points");
                                            dos.writeUTF(turn);
                                        }
                                        if (playerInRound.isIsHopestar()) {
                                            for (Player item : room.getPlayer()) {
                                                dos = new DataOutputStream(item.getSocket().getOutputStream());
                                                dos.writeUTF("(QuizQuestion)" + playerInRound.getListQuiz().get(0).getQuestion());
                                            }
                                            dos = new DataOutputStream(playerInRound.getSocket().getOutputStream());
                                            dos.writeUTF("(AnswerButton)");
                                        } else {
                                            for (Player item : room.getPlayer()) {
                                                dos = new DataOutputStream(item.getSocket().getOutputStream());
                                                dos.writeUTF("(RemoveQuestion)");
                                            }
                                            dos = new DataOutputStream(playerInRound.getSocket().getOutputStream());
                                            dos.writeUTF("(RemoveAnswerButton)");
                                            dos.writeUTF("(HopeStarOption)");
                                        }
                                    } else {
                                        room.getPlayer().get(playerInRoundIndex).setStatus(FinishRound);
                                        check = true;
                                        for (Player item : room.getPlayer()) {
                                            dos = new DataOutputStream(item.getSocket().getOutputStream());
                                            dos.writeUTF("(RemoveQuestion)");
                                        }
                                        for (Player item : room.getPlayer()) {
                                            if (item.getStatus() == NotInRound) {
                                                String round = "(round)" + item.getName();
                                                turn = "(turn)" + item.getName();
                                                for (Player item1 : room.getPlayer()) {
                                                    dos = new DataOutputStream(item1.getSocket().getOutputStream());
                                                    dos.writeUTF(turn);
                                                    dos.writeUTF(round);
                                                }
                                                item.setStatus(InRound);
                                                dos = new DataOutputStream(item.getSocket().getOutputStream());
                                                dos.writeUTF("(package)");
                                                check = false;
                                                break;
                                            }
                                        }
                                        if (check) {
                                            for (Player item : room.getPlayer()) {
                                                dos = new DataOutputStream(item.getSocket().getOutputStream());
                                                dos.writeUTF("(GameFinish)");
                                            }
                                            int temp;
                                            String name;
                                            for (int i = 0; i <= 2; i++) {
                                                for (int j = i + 1; j <= 2; j++) {
                                                    if (room.getPlayer().get(i).getScore() < room.getPlayer().get(j).getScore()) {
                                                        temp = room.getPlayer().get(j).getScore();
                                                        name = room.getPlayer().get(j).getName();
                                                        room.getPlayer().get(j).setName(room.getPlayer().get(i).getName());
                                                        room.getPlayer().get(j).setScore(room.getPlayer().get(i).getScore());
                                                        room.getPlayer().get(i).setName(name);
                                                        room.getPlayer().get(i).setScore(temp);
                                                    }
                                                }
                                            }
                                            for (Player item : room.getPlayer()) {
                                                dos = new DataOutputStream(item.getSocket().getOutputStream());
                                                for (int i = 0; i <= 2; i++) {
                                                    dos.writeUTF("(Name" + (i + 1) + ")" + room.getPlayer().get(i).getName());
                                                    dos.writeUTF("(Score" + (i + 1) + ")" + room.getPlayer().get(i).getScore());
                                                }
                                            }
                                            room.setStatus(FinishPlay);
                                        }
                                    }
                                } else {
                                    if (playerInTurn.equals(playerInRound)) {
                                        if (hopestar) {
                                            room.getPlayer().get(playerInRoundIndex).setScore(playerInRoundScore - questionScore);
                                        }
                                        for (Player item : room.getPlayer()) {
                                            score += item.getName() + " : " + item.getScore() + "_";
                                        }
                                        for (Player item : room.getPlayer()) {
                                            dos = new DataOutputStream(item.getSocket().getOutputStream());
                                            dos.writeUTF(score);
                                        }
                                        dos = new DataOutputStream(playerInRound.getSocket().getOutputStream());
                                        dos.writeUTF("(RemoveAnswerButton)");
                                        turn = "(turn)";
                                        for (Player item : room.getPlayer()) {
                                            if (item.equals(playerInRound) == false) {
                                                dos = new DataOutputStream(item.getSocket().getOutputStream());
                                                dos.writeUTF("(AnswerChoice)");
                                            }
                                            dos = new DataOutputStream(item.getSocket().getOutputStream());
                                            dos.writeUTF("(LastAnswer)" + answer + " (Wrong)");
                                            dos.writeUTF(turn);
                                        }
                                    } else {
                                        room.getPlayer().get(playerInTurnIndex).setScore(playerInTurnScore - questionScore / 2);
                                        for (Player item : room.getPlayer()) {
                                            score += item.getName() + " : " + item.getScore() + "_";
                                        }
                                        for (Player item : room.getPlayer()) {
                                            dos = new DataOutputStream(item.getSocket().getOutputStream());
                                            dos.writeUTF(score);
                                        }
                                        dos = new DataOutputStream(playerInTurn.getSocket().getOutputStream());
                                        dos.writeUTF("(RemoveAnswerButton)");
                                        room.getPlayer().get(playerInRoundIndex).getListQuiz().remove(0);
                                        for (Player item : room.getPlayer()) {
                                            dos = new DataOutputStream(item.getSocket().getOutputStream());
                                            dos.writeUTF("(LastAnswer)" + answer + " (Wrong)");
                                            dos.writeUTF("(CorrectAnswer)" + correctAnswer);
                                            dos.writeUTF("(HopeStar)No");
                                        }
                                        if (playerInRound.getListQuiz().size() > 0) {
                                            turn = "(turn)" + playerInRound.getName();
                                            for (Player item : room.getPlayer()) {
                                                int quiznum = 5 - playerInRound.getListQuiz().size();
                                                dos = new DataOutputStream(item.getSocket().getOutputStream());
                                                dos.writeUTF("(QuizNumber)Question " + quiznum + ":");
                                                dos.writeUTF("(QuizPoint)" + playerInRound.getListQuiz().get(0).getScore() + " points");
                                                dos.writeUTF(turn);
                                            }
                                            if (playerInRound.isIsHopestar()) {
                                                for (Player item : room.getPlayer()) {
                                                    dos = new DataOutputStream(item.getSocket().getOutputStream());
                                                    dos.writeUTF("(QuizQuestion)" + playerInRound.getListQuiz().get(0).getQuestion());
                                                }
                                                dos = new DataOutputStream(playerInRound.getSocket().getOutputStream());
                                                dos.writeUTF("(AnswerButton)");
                                            } else {
                                                for (Player item : room.getPlayer()) {
                                                    dos = new DataOutputStream(item.getSocket().getOutputStream());
                                                    dos.writeUTF("(RemoveQuestion)");
                                                }
                                                dos = new DataOutputStream(playerInRound.getSocket().getOutputStream());
                                                dos.writeUTF("(HopeStarOption)");
                                            }
                                        } else {
                                            room.getPlayer().get(playerInRoundIndex).setStatus(FinishRound);
                                            check = true;
                                            for (Player item : room.getPlayer()) {
                                                dos = new DataOutputStream(item.getSocket().getOutputStream());
                                                dos.writeUTF("(RemoveQuestion)");
                                            }
                                            for (Player item : room.getPlayer()) {
                                                if (item.getStatus() == NotInRound) {
                                                    turn = "(turn)" + item.getName();
                                                    String round = "(round)" + item.getName();
                                                    for (Player item1 : room.getPlayer()) {
                                                        dos = new DataOutputStream(item1.getSocket().getOutputStream());
                                                        dos.writeUTF(turn);
                                                        dos.writeUTF(round);
                                                    }
                                                    item.setStatus(InRound);
                                                    dos = new DataOutputStream(item.getSocket().getOutputStream());
                                                    dos.writeUTF("(package)");
                                                    check = false;
                                                    break;
                                                }
                                            }
                                            if (check) {
                                                for (Player item : room.getPlayer()) {
                                                    dos = new DataOutputStream(item.getSocket().getOutputStream());
                                                    dos.writeUTF("(GameFinish)");
                                                }
                                                int temp;
                                                String name;
                                                for (int i = 0; i <= 2; i++) {
                                                    for (int j = i + 1; j <= 2; j++) {
                                                        if (room.getPlayer().get(i).getScore() < room.getPlayer().get(j).getScore()) {
                                                            temp = room.getPlayer().get(j).getScore();
                                                            name = room.getPlayer().get(j).getName();
                                                            room.getPlayer().get(j).setName(room.getPlayer().get(i).getName());
                                                            room.getPlayer().get(j).setScore(room.getPlayer().get(i).getScore());
                                                            room.getPlayer().get(i).setName(name);
                                                            room.getPlayer().get(i).setScore(temp);
                                                        }
                                                    }
                                                }
                                                for (Player item : room.getPlayer()) {
                                                    dos = new DataOutputStream(item.getSocket().getOutputStream());
                                                    for (int i = 0; i <= 2; i++) {
                                                        dos.writeUTF("(Name" + (i + 1) + ")" + room.getPlayer().get(i).getName());
                                                        dos.writeUTF("(Score" + (i + 1) + ")" + room.getPlayer().get(i).getScore());
                                                    }
                                                }
                                                room.setStatus(FinishPlay);
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                    for (int i = 0; i < Server.listRoom.size(); i++) {
                        if (Server.listRoom.get(i).getStatus() == FinishPlay) {
                            Server.listRoom.remove(i);
                        }
                    }
                }
                if (sms.startsWith("(AnswerChoice)")) {
                    DataOutputStream dos;
                    String turn = "";
                    for (Room room : Server.listRoom) {
                        for (Player player : room.getPlayer()) {
                            if (player.getSocket().getPort() == server.getPort() && room.getStatus() == 1) {
                                Player playerInRound = new Player();
                                for (Player item : room.getPlayer()) {
                                    if (item.getStatus() == InRound) {
                                        playerInRound = item;
                                        break;
                                    }
                                }
                                int playerInRoundIndex = room.getPlayer().indexOf(playerInRound);
                                switch (sms.substring(14)) {
                                    case "Accept":
                                        turn = "(turn)" + player.getName();
                                        for (Player item : room.getPlayer()) {
                                            item.setIsDeny(false);
                                            dos = new DataOutputStream(item.getSocket().getOutputStream());
                                            dos.writeUTF("(AnswerChoice)Done");
                                            dos.writeUTF(turn);
                                        }
                                        dos = new DataOutputStream(player.getSocket().getOutputStream());
                                        dos.writeUTF("(AnswerButton)");
                                        break;
                                    case "Deny":
                                        player.setIsDeny(true);
                                        dos = new DataOutputStream(player.getSocket().getOutputStream());
                                        dos.writeUTF("(AnswerChoice)Done");
                                        int count = 0;
                                        for (Player item : room.getPlayer()) {
                                            if (item.isIsDeny() == true) {
                                                count++;
                                            }
                                        }
                                        if (count == 2) {
                                            for (Player item : room.getPlayer()) {
                                                item.setIsDeny(false);
                                            }
                                            String correctAnswer = playerInRound.getListQuiz().get(0).getAnswer().trim();
                                            room.getPlayer().get(playerInRoundIndex).getListQuiz().remove(0);

                                            for (Player item : room.getPlayer()) {
                                                dos = new DataOutputStream(item.getSocket().getOutputStream());
                                                dos.writeUTF("(CorrectAnswer)" + correctAnswer);
                                                dos.writeUTF("(HopeStar)No");
                                            }
                                            if (playerInRound.getListQuiz().size() > 0) {
                                                for (Player item : room.getPlayer()) {
                                                    int quiznum = 5 - playerInRound.getListQuiz().size();
                                                    dos = new DataOutputStream(item.getSocket().getOutputStream());
                                                    turn = "(turn)" + playerInRound.getName();
                                                    dos.writeUTF(turn);
                                                    dos.writeUTF("(QuizNumber)Question " + quiznum + ":");
                                                    dos.writeUTF("(QuizPoint)" + playerInRound.getListQuiz().get(0).getScore() + " points");
                                                }
                                                if (playerInRound.isIsHopestar()) {
                                                    for (Player item : room.getPlayer()) {
                                                        dos = new DataOutputStream(item.getSocket().getOutputStream());
                                                        dos.writeUTF("(QuizQuestion)" + playerInRound.getListQuiz().get(0).getQuestion());
                                                    }
                                                    dos = new DataOutputStream(playerInRound.getSocket().getOutputStream());
                                                    dos.writeUTF("(AnswerButton)");
                                                } else {
                                                    for (Player item : room.getPlayer()) {
                                                        dos = new DataOutputStream(item.getSocket().getOutputStream());
                                                        dos.writeUTF("(RemoveQuestion)");
                                                    }
                                                    dos = new DataOutputStream(playerInRound.getSocket().getOutputStream());
                                                    dos.writeUTF("(HopeStarOption)");
                                                }
                                            } else {
                                                room.getPlayer().get(playerInRoundIndex).setStatus(FinishRound);
                                                dos = new DataOutputStream(playerInRound.getSocket().getOutputStream());
                                                dos.writeUTF("(RemoveAnswerButton)");
                                                check = true;
                                                for (Player item : room.getPlayer()) {
                                                    dos = new DataOutputStream(item.getSocket().getOutputStream());
                                                    dos.writeUTF("(RemoveQuestion)");
                                                }
                                                for (Player item : room.getPlayer()) {
                                                    if (item.getStatus() == NotInRound) {
                                                        turn = "(turn)" + item.getName();
                                                        String round = "(round)" + item.getName();
                                                        item.setStatus(InRound);
                                                        for (Player item1 : room.getPlayer()) {
                                                            dos = new DataOutputStream(item1.getSocket().getOutputStream());
                                                            dos.writeUTF(turn);
                                                            dos.writeUTF(round);
                                                        }
                                                        dos = new DataOutputStream(item.getSocket().getOutputStream());
                                                        dos.writeUTF("(package)");
                                                        check = false;
                                                        break;
                                                    }
                                                }
                                                if (check) {
                                                    for (Player item : room.getPlayer()) {
                                                        dos = new DataOutputStream(item.getSocket().getOutputStream());
                                                        dos.writeUTF("(GameFinish)");
                                                    }
                                                    int temp;
                                                    String name;
                                                    for (int i = 0; i <= 2; i++) {
                                                        for (int j = i + 1; j <= 2; j++) {
                                                            if (room.getPlayer().get(i).getScore() < room.getPlayer().get(j).getScore()) {
                                                                temp = room.getPlayer().get(j).getScore();
                                                                name = room.getPlayer().get(j).getName();
                                                                room.getPlayer().get(j).setName(room.getPlayer().get(i).getName());
                                                                room.getPlayer().get(j).setScore(room.getPlayer().get(i).getScore());
                                                                room.getPlayer().get(i).setName(name);
                                                                room.getPlayer().get(i).setScore(temp);
                                                            }
                                                        }
                                                    }
                                                    for (Player item : room.getPlayer()) {
                                                        dos = new DataOutputStream(item.getSocket().getOutputStream());
                                                        for (int i = 0; i <= 2; i++) {
                                                            dos.writeUTF("(Name" + (i + 1) + ")" + room.getPlayer().get(i).getName());
                                                            dos.writeUTF("(Score" + (i + 1) + ")" + room.getPlayer().get(i).getScore());
                                                        }
                                                    }
                                                    room.setStatus(FinishPlay);
                                                }
                                            }
                                        }
                                        break;
                                }
                                break;
                            }
                        }
                    }
                    for (int i = 0; i < Server.listRoom.size(); i++) {
                        if (Server.listRoom.get(i).getStatus() == FinishPlay) {
                            Server.listRoom.remove(i);
                        }
                    }
                }
                if (sms.equals("(continue)")) {
                    DataOutputStream dos = new DataOutputStream(server.getOutputStream());
                    dos.writeUTF(sms);
                }
                /*for (Socket item : Server.listSK) {
                 if (item.getPort() != server.getPort()) {
                 DataOutputStream dos = new DataOutputStream(item.getOutputStream());
                 dos.writeUTF(server + " : " + sms);
                 System.out.println(server + " : " + sms);
                 }
                 }*/

            }

        } catch (Exception e) {
            System.out.println("client disconnect " + server);
            try {
                for (Room room : Server.listRoom) {
                    for (Player item : room.getPlayer()) {
                        if (item.getSocket().getPort() == server.getPort()) {
                            room.getPlayer().remove(item);
                            for (Player item1 : room.getPlayer()) {
                                DataOutputStream dos = new DataOutputStream(item1.getSocket().getOutputStream());
                                dos.writeUTF("(Send)" + item.getName() + " has left the room\n");
                                for (int i = 0; i < 3; i++) {
                                    if (i < room.getPlayer().size()) {
                                        dos.writeUTF("(Player" + (i + 1) + ")" + room.getPlayer().get(i).getName());
                                    } else {
                                        dos.writeUTF("(Player" + (i + 1) + ")");
                                    }
                                }
                            }
                            if (room.getStatus() == InPlay) {
                                for (Player item1 : room.getPlayer()) {
                                    DataOutputStream dos = new DataOutputStream(item1.getSocket().getOutputStream());
                                    dos.writeUTF("(GameDisrupt)");
                                }
                                room.setStatus(FinishPlay);
                            }
                            break;
                        }
                    }
                }
                for (int i = 0; i < Server.listRoom.size(); i++) {
                    if (Server.listRoom.get(i).getStatus() == FinishPlay) {
                        Server.listRoom.remove(i);
                    }
                }
                server.close();
                Server.listSK.remove(server);

            } catch (IOException ex) {
                System.out.println("Disconnect to Server");
            }
        }
    }
}
