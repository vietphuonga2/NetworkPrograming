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
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author H
 */
public class Client extends javax.swing.JFrame {

    /**
     * Creates new form Client
     */
    static Socket s;
    static DataInputStream din;
    static DataOutputStream dout;

    public Client() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        msgRoom = new javax.swing.JTextField();
        msgJoin = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        msgName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        msgCreate = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        msgArea = new javax.swing.JTextArea();
        msgList = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        msgRoom.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        msgRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                msgRoomActionPerformed(evt);
            }
        });

        msgJoin.setFont(new java.awt.Font("Segoe Print", 1, 18)); // NOI18N
        msgJoin.setText("Join");
        msgJoin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                msgJoinActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe Print", 1, 18)); // NOI18N
        jLabel1.setText("    Room");

        msgName.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Segoe Print", 1, 18)); // NOI18N
        jLabel2.setText("    Name");

        msgCreate.setFont(new java.awt.Font("Segoe Print", 1, 18)); // NOI18N
        msgCreate.setText("Create");
        msgCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                msgCreateActionPerformed(evt);
            }
        });

        msgArea.setColumns(20);
        msgArea.setFont(new java.awt.Font("Segoe Print", 1, 16)); // NOI18N
        msgArea.setRows(5);
        jScrollPane2.setViewportView(msgArea);

        msgList.setFont(new java.awt.Font("Segoe Print", 1, 18)); // NOI18N
        msgList.setText("List ");
        msgList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                msgListActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(msgRoom, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(173, 173, 173))
            .addGroup(layout.createSequentialGroup()
                .addGap(250, 250, 250)
                .addComponent(msgList, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(84, 84, 84)
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(msgJoin, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(msgCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(msgName, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(172, 172, 172))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(msgList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(msgRoom, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(msgName, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(msgCreate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(msgJoin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void msgJoinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_msgJoinActionPerformed
        try {
            if (msgRoom.getText().length() > 0 && msgRoom.getText().charAt(0)!= ' ' && msgName.getText().length() > 0 && msgName.getText().charAt(0)!= ' ') {
                String msgout = "join_";
                msgout = msgout.concat(msgRoom.getText().trim() + "_" + msgName.getText().trim());
                dout.writeUTF(msgout);
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_msgJoinActionPerformed

    private void msgRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_msgRoomActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_msgRoomActionPerformed

    private void msgListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_msgListActionPerformed
        try {
            dout.writeUTF("list");
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_msgListActionPerformed

    private void msgCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_msgCreateActionPerformed
        try {
            if (msgRoom.getText().length() > 0 && msgRoom.getText().charAt(0)!= ' ' && msgName.getText().length() > 0 && msgName.getText().charAt(0)!= ' ') {
                String msgout = "create_";
                msgout = msgout.concat(msgRoom.getText().trim() + "_" + msgName.getText().trim());
                dout.writeUTF(msgout);
            }
        } catch (Exception e) {
            System.out.println("error");
        }
    }//GEN-LAST:event_msgCreateActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        Client client = new Client();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {

                client.setVisible(true);
            }
        });
        try {
            s = new Socket(InetAddress.getLocalHost(), 3000);
            din = new DataInputStream(s.getInputStream());
            dout = new DataOutputStream(s.getOutputStream());
            String msg = "";
            Game game = new Game();
            Gameplay gameplay = new Gameplay();
            Gamepackage gamepackage = new Gamepackage();
            Gamefinish gamefinish = new Gamefinish();
            gameplay.getMsgAnswer().setVisible(false);
            gameplay.getMsgAnswerAccept().setVisible(false);
            gameplay.getMsgAnswerDeny().setVisible(false);
            gameplay.getMsgHopestarNo().setVisible(false);
            gameplay.getMsgHopestarYes().setVisible(false);
            gameplay.getMsgQuizHopeStar().setVisible(false);
            int count = 0;
            while (!msg.equals("exit")) {
                msg = din.readUTF();

                if (count == 0) {
                    if (msg.equals("Success")) {
                        game.setVisible(true);
                        client.setVisible(false);
                        count = 1;
                    }
                    if( msg.startsWith("(List)")){
                        msgArea.setText(msgArea.getText() + " Server:\t" + msg.substring(6));
                    }
                    if(msg.equals("(Create)Fail")){
                        msgArea.setText(msgArea.getText() + " Server:\t" + "Create room fail !\n");
                    }
                    if(msg.equals("(Join)Fail")){
                        msgArea.setText(msgArea.getText() + " Server:\t" + "Join room fail !\n");
                    }
                } else if (count == 1) {

                    if (msg.equals("leave")) {
                        game.setVisible(false);
                        client.setVisible(true);
                        count = 0;
                    } 
                    if (msg.equals("start")) {
                        game.setVisible(false);
                        gameplay.setVisible(true);
                        gameplay.getMsgAnswer().setVisible(false);
                        gameplay.getMsgAnswerAccept().setVisible(false);
                        gameplay.getMsgAnswerDeny().setVisible(false);
                        gameplay.getMsgHopestarNo().setVisible(false);
                        gameplay.getMsgHopestarYes().setVisible(false);
                        gameplay.getMsgQuizHopeStar().setVisible(false);
                        gameplay.getMsgLastAnswer().setText("");
                        gameplay.getMsgCorrectAnswer().setText("");
                        gameplay.getMsgQuizQuestion().setText("");
                        gameplay.getMsgQuizNumber().setText("Question :");
                        gameplay.getMsgQuizPoint().setText("");
                        gameplay.getMsgPackage().setText("");
                        count = 2;
                    } 
                    if(msg.startsWith("(Player1)")){
                        game.getMsgPlayer1().setText(msg.substring(9));
                    }
                    if(msg.startsWith("(Player2)")){
                        game.getMsgPlayer2().setText(msg.substring(9));
                    }
                    if(msg.startsWith("(Player3)")){
                        game.getMsgPlayer3().setText(msg.substring(9));
                    }
                    if(msg.startsWith("(Welcome)")){
                        game.getMsgArea().setText(msg.substring(9));
                    }
                    if(msg.startsWith("(Send)")){
                        game.getMsgArea().setText(game.getMsgArea().getText() + msg.substring(6));
                    }
                    if(msg.startsWith("(RoomName)")){
                        game.getMsgRoomName().setText(msg.substring(10));
                    }
                } else if (count == 2) {
                    if (msg.startsWith("(scoreboard)")) {
                        msg = msg.substring(12);
                        String[] msgScore = msg.split("_");
                        gameplay.getMsgScore1().setText(msgScore[0]);
                        gameplay.getMsgScore2().setText(msgScore[1]);
                        gameplay.getMsgScore3().setText(msgScore[2]);
                    }
                    if (msg.startsWith("(turn)")) {
                        gameplay.getMsgTurn().setText(msg.substring(6));
                    }
                    if (msg.startsWith("(round)")) {
                        gameplay.getMsgRound().setText(msg.substring(7));
                    }
                    if (msg.startsWith("(PackagePoint)")) {
                        gameplay.getMsgPackage().setText(msg.substring(14));
                    }
                    if (msg.startsWith("(QuizNumber)")) {
                        gameplay.getMsgQuizNumber().setText(msg.substring(12));
                    }
                    if (msg.startsWith("(QuizPoint)")) {
                        gameplay.getMsgQuizPoint().setText(msg.substring(11));
                    }
                    if (msg.startsWith("(QuizQuestion)")) {
                        String question = msg.substring(14);
                        StringBuilder str = new StringBuilder(question);
                        if (str.length() > 68 && str.length() <= 136) {
                            for (int i = 60; i <= 68; i++) {
                                if (str.charAt(i) == ' ') {
                                    str.setCharAt(i, '\n');
                                    break;
                                }
                            }
                        } else if (str.length() > 136 && str.length() <= 204) {
                            for (int i = 60; i <= 68; i++) {
                                if (str.charAt(i) == ' ') {
                                    str.setCharAt(i, '\n');
                                    break;
                                }
                            }
                            for (int i = 128; i <= 136; i++) {
                                if (str.charAt(i) == ' ') {
                                    str.setCharAt(i, '\n');
                                    break;
                                }
                            }
                        } else if (str.length() > 204) {
                            for (int i = 60; i <= 68; i++) {
                                if (str.charAt(i) == ' ') {
                                    str.setCharAt(i, '\n');
                                    break;
                                }
                            }
                            for (int i = 128; i <= 136; i++) {
                                if (str.charAt(i) == ' ') {
                                    str.setCharAt(i, '\n');
                                    break;
                                }
                            }
                            for (int i = 196; i <= 204; i++) {
                                if (str.charAt(i) == ' ') {
                                    str.setCharAt(i, '\n');
                                    break;
                                }
                            }
                        }
                        gameplay.getMsgQuizQuestion().setText(str.toString());
                    }
                    if (msg.startsWith("(package)")) {
                        gameplay.setVisible(false);
                        gamepackage.setVisible(true);
                        count = 3;
                    }
                    if (msg.equals("(AnswerButton)")) {
                        gameplay.getMsgAnswer().setVisible(true);
                    }
                    if (msg.equals("(HopeStarOption)")) {
                        gameplay.getMsgHopestarNo().setVisible(true);
                        gameplay.getMsgHopestarYes().setVisible(true);
                    }
                    if (msg.equals("(HopeStar)Yes")) {
                        gameplay.getMsgQuizHopeStar().setVisible(true);
                        gameplay.getMsgHopestarNo().setVisible(false);
                        gameplay.getMsgHopestarYes().setVisible(false);
                    }
                    if (msg.equals("(HopeStar)No")) {
                        gameplay.getMsgQuizHopeStar().setVisible(false);
                        gameplay.getMsgHopestarNo().setVisible(false);
                        gameplay.getMsgHopestarYes().setVisible(false);
                    }
                    if (msg.startsWith("(LastAnswer)")) {
                        gameplay.getMsgLastAnswer().setText(msg.substring(12));
                    }
                    if (msg.startsWith("(CorrectAnswer)")) {
                        gameplay.getMsgCorrectAnswer().setText(msg.substring(15));
                    }
                    if (msg.equals("(RemoveAnswerButton)")) {
                        gameplay.getMsgAnswer().setVisible(false);
                    }
                    if (msg.equals("(AnswerChoice)")) {
                        gameplay.getMsgAnswerAccept().setVisible(true);
                        gameplay.getMsgAnswerDeny().setVisible(true);
                    }
                    if (msg.equals("(AnswerChoice)Done")) {
                        gameplay.getMsgAnswerAccept().setVisible(false);
                        gameplay.getMsgAnswerDeny().setVisible(false);
                    }
                    if (msg.equals("(RemoveQuestion)")) {
                        gameplay.getMsgQuizQuestion().setText("");
                    }
                    if (msg.equals("(GameFinish)")) {
                        gameplay.setVisible(false);
                        gamefinish.setVisible(true);
                        count = 4;
                    }
                    if (msg.startsWith("(GameDisrupt)")) {
                        client.setVisible(true);
                        gameplay.setVisible(false);
                        count = 0;
                    }
                } else if (count == 3) {
                    if (msg.startsWith("done")) {
                        gameplay.setVisible(true);
                        gamepackage.setVisible(false);
                        count = 2;
                    }
                    if (msg.startsWith("(GameDisrupt)")) {
                        client.setVisible(true);
                        gamepackage.setVisible(false);
                        count = 0;
                    }
                } else if (count == 4) {
                    if (msg.startsWith("(continue)")) {
                        gamefinish.setVisible(false);
                        client.setVisible(true);
                        count = 0;
                    }
                    if (msg.startsWith("(Name1)")) {
                        gamefinish.getMsgName1().setText(msg.substring(7));
                    }
                    if (msg.startsWith("(Name2)")) {
                        gamefinish.getMsgName2().setText(msg.substring(7));
                    }
                    if (msg.startsWith("(Name3)")) {
                        gamefinish.getMsgName3().setText(msg.substring(7));
                    }
                    if (msg.startsWith("(Score1)")) {
                        gamefinish.getMsgScore1().setText(msg.substring(8));
                    }
                    if (msg.startsWith("(Score2)")) {
                        gamefinish.getMsgScore2().setText(msg.substring(8));
                    }
                    if (msg.startsWith("(Score3)")) {
                        gamefinish.getMsgScore3().setText(msg.substring(8));
                    }
                }

            }
        } catch (Exception e) {
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane2;
    private static javax.swing.JTextArea msgArea;
    private javax.swing.JButton msgCreate;
    private javax.swing.JButton msgJoin;
    private javax.swing.JButton msgList;
    private javax.swing.JTextField msgName;
    private javax.swing.JTextField msgRoom;
    // End of variables declaration//GEN-END:variables
}