/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package olympia;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
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
        InetAddress addr = InetAddress.getByName("192.168.2.200");
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
                if (!sms.startsWith("(Time)")) {
                    System.out.println(server + " : " + sms);
                }
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
                        System.out.println("Server: Create room successfully ! ");
                        System.out.println("Server: (PlayerName)" + player.getName());
                        System.out.println("Server: " + "(RoomName)" + room.getName());
                    } else {
                        DataOutputStream dos = new DataOutputStream(server.getOutputStream());
                        dos.writeUTF("(Create)Fail");
                        System.out.println("Server: Create room fail");
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
                            System.out.println("Server: Join room successfully ! ");
                            System.out.println("Server: (PlayerName)" + player.getName());
                            System.out.println("Server: " + "(RoomName)" + room.getName());
                            check = true;
                            break;
                        }
                    }
                    if (check == false) {
                        DataOutputStream dos = new DataOutputStream(server.getOutputStream());
                        dos.writeUTF("(Join)Fail");
                        System.out.println("Server: Join room fail !");
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
                    System.out.println("Server: " + smsList + "\n");
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
                                System.out.println("Server: " + item.getName() + " has left room :"+room.getName());
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
                                }
                                System.out.println("Server: (Send)" + player.getName() + " : " + sms.substring(6));
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
                                String playerName = "(PlayerName)";
                                String playerName1= "";
                                String turn = "(turn)" + room.getPlayer().get(0).getName();
                                String round = "(round)" + room.getPlayer().get(0).getName();
                                room.getPlayer().get(0).setStatus(InRound);
                                DataOutputStream dos;
                                for (Player player1 : room.getPlayer()) {
                                    dos = new DataOutputStream(player1.getSocket().getOutputStream());
                                    dos.writeUTF("start");
                                    score += player1.getName() + " : " + player1.getScore() + "_";
                                    playerName1+=player1.getName()+ " ";
                                }
                                for (Player item : room.getPlayer()) {
                                    dos = new DataOutputStream(item.getSocket().getOutputStream());
                                    dos.writeUTF(playerName + item.getName());
                                    dos.writeUTF(score);
                                    dos.writeUTF(turn);
                                    dos.writeUTF(round);
                                }
                                dos = new DataOutputStream(room.getPlayer().get(0).getSocket().getOutputStream());
                                dos.writeUTF("(package)");
                                System.out.println("Server: Start game successfully !");
                                System.out.println("Server: (RoomName)"+room.getName());
                                System.out.println("Server: (ScoreBoard)"+score);
                                System.out.println("Server: (PlayerRound)"+room.getPlayer().get(0).getName());
                                System.out.println("Server: (PlayerTurn)"+room.getPlayer().get(0).getName());
                                break;
                            }
                        }
                    }
                    if (check == false) {
                        DataOutputStream dos = new DataOutputStream(server.getOutputStream());
                        dos.writeUTF("(Send)Start room fail");
                        System.out.println("Server: Start room fail !");
                    }
                }
                if (sms.startsWith("(package)")) {
                    DataOutputStream dos;
                    Random generator = new Random();
                    dos = new DataOutputStream(server.getOutputStream());
                    dos.writeUTF("done");
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
                                int quiznum = 5 - player.getListQuiz().size();
                                for (Player item : room.getPlayer()) {
                                    dos = new DataOutputStream(item.getSocket().getOutputStream());
                                    dos.writeUTF("(Time)");
                                    dos.writeUTF("(PackagePoint)" + sms.substring(9));
                                    dos.writeUTF("(QuizNumber)Question " + " " + quiznum + ":");
                                    dos.writeUTF("(QuizPoint)" + player.getListQuiz().get(0).getScore() + " points");
                                }
                                System.out.println("Server: " + "(PackagePoint)" + sms.substring(9));
                                System.out.println("Server: " + "(QuizNumber)Question " + " " + quiznum + ":");
                                System.out.println("Server: " + "(QuizPoint)" + player.getListQuiz().get(0).getScore() + " points");
                                dos = new DataOutputStream(server.getOutputStream());
                                dos.writeUTF("(HopeStarOption)");
                                System.out.println("Server: (HopeStarOption)"+player.getName());
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
                                dos.writeUTF("(AnswerButton)" + player.getListQuiz().get(0).getTime());
                                System.out.println("Server: (AnswerTime)"+player.getListQuiz().get(0).getTime());
                                for (Player item : room.getPlayer()) {
                                    dos = new DataOutputStream(item.getSocket().getOutputStream());
                                    dos.writeUTF(sms);
                                    dos.writeUTF("(QuizQuestion)" + player.getListQuiz().get(0).getQuestion());
                                }
                                System.out.println("Server: " + sms);
                                System.out.println("Server: " + "(QuizQuestion)" + player.getListQuiz().get(0).getQuestion());
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
                                            System.out.println("Server: (RemoveAnswerButton)");
                                            room.getPlayer().get(playerInTurnIndex).setScore(playerInTurnScore + questionScore);
                                        }
                                    } else {
                                        if (playerInTurn.equals(playerInRound)) {
                                            room.getPlayer().get(playerInTurnIndex).setScore(playerInRoundScore + questionScore);
                                        } else {
                                            dos = new DataOutputStream(playerInTurn.getSocket().getOutputStream());
                                            dos.writeUTF("(RemoveAnswerButton)");
                                            System.out.println("Server: (RemoveAnswerButton)");
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
                                    System.out.println("Server: (LastAnswer)" + answer + " (Correct)");
                                    System.out.println("Server: (CorrectAnswer)" + correctAnswer);
                                    if (playerInRound.getListQuiz().size() > 0) {
                                        int quiznum = 5 - playerInRound.getListQuiz().size();
                                        turn = "(turn)" + playerInRound.getName();
                                        for (Player item : room.getPlayer()) {
                                            dos = new DataOutputStream(item.getSocket().getOutputStream());
                                            dos.writeUTF("(QuizNumber)Question " + quiznum + ":");
                                            dos.writeUTF("(QuizPoint)" + playerInRound.getListQuiz().get(0).getScore() + " points");
                                            dos.writeUTF(turn);
                                        }
                                        System.out.println("Server: " + "(QuizNumber)Question " + quiznum + ":");
                                        System.out.println("Server: " + "(QuizPoint)" + playerInRound.getListQuiz().get(0).getScore() + " points");
                                        System.out.println("Server: " + turn);
                                        if (playerInRound.isIsHopestar()) {
                                            for (Player item : room.getPlayer()) {
                                                dos = new DataOutputStream(item.getSocket().getOutputStream());
                                                dos.writeUTF("(QuizQuestion)" + playerInRound.getListQuiz().get(0).getQuestion());
                                            }
                                            System.out.println("Server: " + "(QuizQuestion)" + playerInRound.getListQuiz().get(0).getQuestion());
                                            dos = new DataOutputStream(playerInRound.getSocket().getOutputStream());
                                            dos.writeUTF("(AnswerButton)" + playerInRound.getListQuiz().get(0).getTime());
                                            System.out.println("Server: (AnswerButton)");
                                        } else {
                                            for (Player item : room.getPlayer()) {
                                                dos = new DataOutputStream(item.getSocket().getOutputStream());
                                                dos.writeUTF("(RemoveQuestion)");
                                            }
                                            dos = new DataOutputStream(playerInRound.getSocket().getOutputStream());
                                            dos.writeUTF("(RemoveAnswerButton)");
                                            dos.writeUTF("(HopeStarOption)");
                                            System.out.println("Server: (RemoveQuestion)");
                                            System.out.println("Server: (RemoveAnswerButton)");
                                            System.out.println("Server: (HopeStarOption)");
                                        }
                                    } else {
                                        room.getPlayer().get(playerInRoundIndex).setStatus(FinishRound);
                                        dos = new DataOutputStream(playerInRound.getSocket().getOutputStream());
                                        dos.writeUTF("(RemoveAnswerButton)");
                                        System.out.println("Server: (RemoveAnswerButton)");
                                        check = true;
                                        for (Player item : room.getPlayer()) {
                                            dos = new DataOutputStream(item.getSocket().getOutputStream());
                                            dos.writeUTF("(RemoveQuestion)");
                                            dos.writeUTF("(PackagePoint)");
                                            dos.writeUTF("(QuizNumber)Question ");
                                            dos.writeUTF("(QuizPoint)");
                                            dos.writeUTF("(HopeStar)No");
                                        }
                                        System.out.println("Server: (RemoveQuestion)");
                                        for (Player item : room.getPlayer()) {
                                            if (item.getStatus() == NotInRound) {
                                                String round = "(round)" + item.getName();
                                                turn = "(turn)" + item.getName();
                                                for (Player item1 : room.getPlayer()) {
                                                    dos = new DataOutputStream(item1.getSocket().getOutputStream());
                                                    dos.writeUTF(turn);
                                                    dos.writeUTF(round);
                                                }
                                                System.out.println("Server: " + round);
                                                System.out.println("Server: " + turn);
                                                item.setStatus(InRound);
                                                dos = new DataOutputStream(item.getSocket().getOutputStream());
                                                dos.writeUTF("(package)");
                                                System.out.println("Server: (package)");
                                                check = false;
                                                break;
                                            }
                                        }
                                        if (check) {
                                            for (Player item : room.getPlayer()) {
                                                dos = new DataOutputStream(item.getSocket().getOutputStream());
                                                dos.writeUTF("(GameFinish)");
                                            }
                                            System.out.println("Server: (GameFinish)");
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
                                        System.out.println("Server: (RemoveAnswerButton)");
                                        turn = "(turn)";
                                        for (Player item : room.getPlayer()) {
                                            if (item.equals(playerInRound) == false) {
                                                dos = new DataOutputStream(item.getSocket().getOutputStream());
                                                dos.writeUTF("(AnswerChoice)");
                                                System.out.println("Server: (AnswerChoice)"+item.getName());
                                            }
                                            dos = new DataOutputStream(item.getSocket().getOutputStream());
                                            dos.writeUTF("(LastAnswer)" + answer + " (Wrong)");
                                            dos.writeUTF(turn);
                                        }
                                        System.out.println("Server: " + "(LastAnswer)" + answer + " (Wrong)");
                                        System.out.println("Server: " + turn);
                                    } else {
                                        room.getPlayer().get(playerInTurnIndex).setScore(playerInTurnScore - questionScore / 2);
                                        for (Player item : room.getPlayer()) {
                                            score += item.getName() + " : " + item.getScore() + "_";
                                        }
                                        for (Player item : room.getPlayer()) {
                                            dos = new DataOutputStream(item.getSocket().getOutputStream());
                                            dos.writeUTF(score);
                                        }
                                        System.out.println("Server: " + score);
                                        dos = new DataOutputStream(playerInTurn.getSocket().getOutputStream());
                                        dos.writeUTF("(RemoveAnswerButton)");
                                        System.out.println("Server: (RemoveAnswerButton)");
                                        room.getPlayer().get(playerInRoundIndex).getListQuiz().remove(0);
                                        for (Player item : room.getPlayer()) {
                                            dos = new DataOutputStream(item.getSocket().getOutputStream());
                                            dos.writeUTF("(LastAnswer)" + answer + " (Wrong)");
                                            dos.writeUTF("(CorrectAnswer)" + correctAnswer);
                                            dos.writeUTF("(HopeStar)No");
                                        }
                                        System.out.println("Server: " + "(LastAnswer)" + answer + " (Wrong)");
                                        System.out.println("Server: " + "(CorrectAnswer)" + correctAnswer);
                                        if (playerInRound.getListQuiz().size() > 0) {
                                            turn = "(turn)" + playerInRound.getName();
                                            int quiznum = 5 - playerInRound.getListQuiz().size();
                                            for (Player item : room.getPlayer()) {
                                                dos = new DataOutputStream(item.getSocket().getOutputStream());
                                                dos.writeUTF("(QuizNumber)Question " + quiznum + ":");
                                                dos.writeUTF("(QuizPoint)" + playerInRound.getListQuiz().get(0).getScore() + " points");
                                                dos.writeUTF(turn);
                                            }
                                            System.out.println("Server: " + "(QuizNumber)Question " + quiznum + ":");
                                            System.out.println("Server: " + "(QuizPoint)" + playerInRound.getListQuiz().get(0).getScore() + " points");
                                            System.out.println("Server: " + turn);
                                            if (playerInRound.isIsHopestar()) {
                                                for (Player item : room.getPlayer()) {
                                                    dos = new DataOutputStream(item.getSocket().getOutputStream());
                                                    dos.writeUTF("(QuizQuestion)" + playerInRound.getListQuiz().get(0).getQuestion());
                                                }
                                                System.out.println("Server: " + "(QuizQuestion)" + playerInRound.getListQuiz().get(0).getQuestion());
                                                dos = new DataOutputStream(playerInRound.getSocket().getOutputStream());
                                                dos.writeUTF("(AnswerButton)" + playerInRound.getListQuiz().get(0).getTime());
                                                System.out.println("Server: (AnswerButton)");
                                            } else {
                                                for (Player item : room.getPlayer()) {
                                                    dos = new DataOutputStream(item.getSocket().getOutputStream());
                                                    dos.writeUTF("(RemoveQuestion)");
                                                }
                                                System.out.println("Server: (RemoveQuestion)");
                                                dos = new DataOutputStream(playerInRound.getSocket().getOutputStream());
                                                dos.writeUTF("(HopeStarOption)");
                                                System.out.println("Server: (HopeStarOption)");
                                            }
                                        } else {
                                            room.getPlayer().get(playerInRoundIndex).setStatus(FinishRound);
                                            check = true;
                                            for (Player item : room.getPlayer()) {
                                                dos = new DataOutputStream(item.getSocket().getOutputStream());
                                                dos.writeUTF("(RemoveQuestion)");
                                                dos.writeUTF("(PackagePoint)");
                                                dos.writeUTF("(QuizNumber)Question ");
                                                dos.writeUTF("(QuizPoint)");
                                                dos.writeUTF("(HopeStar)No");
                                            }
                                            System.out.println("Server: (RemoveQuestion)");
                                            for (Player item : room.getPlayer()) {
                                                if (item.getStatus() == NotInRound) {
                                                    turn = "(turn)" + item.getName();
                                                    String round = "(round)" + item.getName();
                                                    for (Player item1 : room.getPlayer()) {
                                                        dos = new DataOutputStream(item1.getSocket().getOutputStream());
                                                        dos.writeUTF(turn);
                                                        dos.writeUTF(round);
                                                    }
                                                    System.out.println("Server: " + turn);
                                                    System.out.println("Server: " + round);
                                                    item.setStatus(InRound);
                                                    dos = new DataOutputStream(item.getSocket().getOutputStream());
                                                    dos.writeUTF("(package)");
                                                    System.out.println("Server: (package)");
                                                    check = false;
                                                    break;
                                                }
                                            }
                                            if (check) {
                                                for (Player item : room.getPlayer()) {
                                                    dos = new DataOutputStream(item.getSocket().getOutputStream());
                                                    dos.writeUTF("(GameFinish)");
                                                }
                                                System.out.println("Server: (GameFinish)");
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
                                                for (int i = 0; i <= 2; i++) {
                                                    System.out.println("(Name" + (i + 1) + ")" + room.getPlayer().get(i).getName());
                                                    System.out.println("(Score" + (i + 1) + ")" + room.getPlayer().get(i).getScore());
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
                                        dos.writeUTF("(AnswerButton)15");
                                        System.out.println("Server: " + turn);
                                        System.out.println("Server: (AnswerTime)15");
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
                                            System.out.println("Server: " + "(CorrectAnswer)" + correctAnswer);
                                            if (playerInRound.getListQuiz().size() > 0) {
                                                int quiznum = 5 - playerInRound.getListQuiz().size();
                                                for (Player item : room.getPlayer()) {
                                                    dos = new DataOutputStream(item.getSocket().getOutputStream());
                                                    turn = "(turn)" + playerInRound.getName();
                                                    dos.writeUTF(turn);
                                                    dos.writeUTF("(QuizNumber)Question " + quiznum + ":");
                                                    dos.writeUTF("(QuizPoint)" + playerInRound.getListQuiz().get(0).getScore() + " points");
                                                }
                                                System.out.println("Server: " + turn);
                                                System.out.println("Server: " + "(QuizNumber)Question " + quiznum + ":");
                                                System.out.println("Server: " + "(QuizPoint)" + playerInRound.getListQuiz().get(0).getScore() + " points");
                                                if (playerInRound.isIsHopestar()) {
                                                    for (Player item : room.getPlayer()) {
                                                        dos = new DataOutputStream(item.getSocket().getOutputStream());
                                                        dos.writeUTF("(QuizQuestion)" + playerInRound.getListQuiz().get(0).getQuestion());
                                                    }
                                                    dos = new DataOutputStream(playerInRound.getSocket().getOutputStream());
                                                    dos.writeUTF("(AnswerButton)" + playerInRound.getListQuiz().get(0).getTime());
                                                    System.out.println("Server: " + "(QuizQuestion)" + playerInRound.getListQuiz().get(0).getQuestion());
                                                    System.out.println("Server: (AnswerTime)"+playerInRound.getListQuiz().get(0).getTime());
                                                } else {
                                                    for (Player item : room.getPlayer()) {
                                                        dos = new DataOutputStream(item.getSocket().getOutputStream());
                                                        dos.writeUTF("(RemoveQuestion)");
                                                    }
                                                    dos = new DataOutputStream(playerInRound.getSocket().getOutputStream());
                                                    dos.writeUTF("(HopeStarOption)");
                                                    System.out.println("Server: (RemoveQuestion)");
                                                    System.out.println("Server: (HopeStarOption)");
                                                }
                                            } else {
                                                room.getPlayer().get(playerInRoundIndex).setStatus(FinishRound);
                                                dos = new DataOutputStream(playerInRound.getSocket().getOutputStream());
                                                dos.writeUTF("(RemoveAnswerButton)");
                                                System.out.println("Server: (RemoveAnswerButton)");
                                                check = true;
                                                for (Player item : room.getPlayer()) {
                                                    dos = new DataOutputStream(item.getSocket().getOutputStream());
                                                    dos.writeUTF("(RemoveQuestion)");
                                                    dos.writeUTF("(PackagePoint)");
                                                    dos.writeUTF("(QuizNumber)Question ");
                                                    dos.writeUTF("(QuizPoint)");
                                                    dos.writeUTF("(HopeStar)No");
                                                }
                                                System.out.println("Server: (RemoveQuestion)");
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
                                                        System.out.println("Server: " + turn);
                                                        System.out.println("Server: " + round);
                                                        System.out.println("Server: (package)"+item.getName());
                                                        break;
                                                    }
                                                }
                                                if (check) {
                                                    for (Player item : room.getPlayer()) {
                                                        dos = new DataOutputStream(item.getSocket().getOutputStream());
                                                        dos.writeUTF("(GameFinish)");
                                                    }
                                                    System.out.println("Server: (GameFinish)");
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
                if (sms.startsWith("(SetAnswerTextEmpty)")) {
                    DataOutputStream dos;
                    for (Room room : Server.listRoom) {
                        for (Player player : room.getPlayer()) {
                            if (player.getSocket().getPort() == server.getPort() && room.getStatus() == 1) {
                                dos = new DataOutputStream(player.getSocket().getOutputStream());
                                dos.writeUTF(sms);
                                break;
                            }
                        }
                    }
                }
                if (sms.startsWith("(Time)")) {
                    DataOutputStream dos;
                    for (Room room : Server.listRoom) {
                        for (Player player : room.getPlayer()) {
                            if (player.getSocket().getPort() == server.getPort() && room.getStatus() == 1) {
                                for (Player item : room.getPlayer()) {
                                    dos = new DataOutputStream(item.getSocket().getOutputStream());
                                    dos.writeUTF(sms);
                                }
                                break;
                            }
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
                            System.out.println("Server: " + "(Send)" + item.getName() + " has left the room");
                            if (room.getStatus() == InPlay) {
                                for (Player item1 : room.getPlayer()) {
                                    DataOutputStream dos = new DataOutputStream(item1.getSocket().getOutputStream());
                                    dos.writeUTF("(GameDisrupt)");
                                }
                                System.out.println("Server: (GameDisrupt)");
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
