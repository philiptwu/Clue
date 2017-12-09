/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clue.gui;

import clue.action.PlayerAction;
import clue.action.PlayerActionChooseToken;
import clue.action.PlayerActionDiscardToken;
import clue.action.PlayerActionEndTurn;
import clue.action.PlayerActionMakeAccusation;
import clue.action.PlayerActionMakeSuggestion;
import clue.action.PlayerActionMove;
import clue.action.PlayerActionShowCard;
import clue.action.PlayerActionVoteStartGame;
import clue.common.Card;
import clue.common.Card.CardType;
import clue.common.GameBoard.MoveDirection;
import clue.common.Room;
import clue.common.Room.RoomId;
import clue.common.RoomCard;
import clue.common.Token;
import clue.common.Weapon;
import clue.common.Token.TokenId;
import clue.common.Weapon.WeaponId;
import clue.ui.GraphicalUI;

import java.awt.Color;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.ListModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 *
 * @author Philip
 */
public class GameJFrame extends javax.swing.JFrame {

    // Member variables
    private GraphicalUI graphicalUI;
	
    /**
     * Creates new form GameJFrame
     */
    public GameJFrame(GraphicalUI graphicalUI) {
    	// Save reference
    	this.graphicalUI = graphicalUI;

    	// Set nimbus visual style
    	//setNimbusStyle();
    	
    	// Initialize components
        initComponents();
        
        // Disable all menus except connection
        disableAllMenuPanels();
        toggleConnectionPanel(true);
    }
    
    private void setNimbusStyle() {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GameJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GameJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GameJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GameJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }
    
    public synchronized void disableAllMenuPanels() {
    	toggleConnectionPanel(false);
    	toggleJoinGamePanel(false);
    	toggleMovePanel(false);
    	toggleSuggestPanel(false);
    	toggleAccusePanel(false);
    	toggleEndTurnPanel(false);
    	toggleShowCardPanel(false);
    	toggleChooseTokenPanel(false);
    	toggleDiscardTokenPanel(false);
    	toggleStartGamePanel(false);
    }
    
    public synchronized void toggleConnectionPanel(boolean b){
        ipAddressLabel.setEnabled(b);
        ipAddressTextField.setEnabled(b);
        portLabel.setEnabled(b);
        portTextField.setEnabled(b);
        connectButton.setEnabled(b);
        connectionPanel.setEnabled(b);
        connectionPanel.repaint();
    }
    
    public synchronized void toggleJoinGamePanel(boolean b) {
    	playerIdLabel.setEnabled(b);
    	playerIdTextField.setEnabled(b);
    	joinGameButton.setEnabled(b);
    	joinGamePanel.setEnabled(b);
    	joinGamePanel.repaint();
    }
    
    public synchronized void toggleMovePanel(boolean b) {
    	northButton.setEnabled(b);
    	secretButton.setEnabled(b);
    	southButton.setEnabled(b);
    	westButton.setEnabled(b);
    	eastButton.setEnabled(b);
    	movePanel.setEnabled(b);
    	movePanel.repaint();
    }
    
    public synchronized void toggleSuggestPanel(boolean b) {
    	suggestTokenLabel.setEnabled(b);
    	suggestWeaponLabel.setEnabled(b);
    	suggestTokenComboBox.setEnabled(b);
    	suggestWeaponComboBox.setEnabled(b);
    	suggestButton.setEnabled(b);
    	suggestPanel.setEnabled(b);
    	suggestPanel.repaint();
    }
    
    public synchronized void toggleAccusePanel(boolean b) {
    	accuseRoomLabel.setEnabled(b);
    	accuseTokenLabel.setEnabled(b);
    	accuseWeaponLabel.setEnabled(b);
    	accuseRoomComboBox.setEnabled(b);
    	accuseTokenComboBox.setEnabled(b);
    	accuseWeaponComboBox.setEnabled(b);
    	accuseButton.setEnabled(b);
    	accusePanel.setEnabled(b);
    	accusePanel.repaint();
    }
    
    public synchronized void toggleEndTurnPanel(boolean b) {
    	endTurnButton.setEnabled(b);
    	endTurnPanel.setEnabled(b);
    	endTurnPanel.repaint();
    }
    
    public synchronized void toggleShowCardPanel(boolean b) {
    	cardLabel.setEnabled(b);
    	cardComboBox.setEnabled(b);
    	showCardButton.setEnabled(b);
    	showCardPanel.setEnabled(b);
    	showCardPanel.repaint();
    }
    
    public synchronized void toggleChooseTokenPanel(boolean b) {
    	chooseTokenLabel.setEnabled(b);
    	chooseTokenComboBox.setEnabled(b);
    	chooseTokenButton.setEnabled(b);
    	chooseTokenPanel.setEnabled(b);
    	chooseTokenPanel.repaint();
    }
    
    public synchronized void toggleDiscardTokenPanel(boolean b) {
    	discardTokenButton.setEnabled(b);
    	discardTokenPanel.setEnabled(b);
    	discardTokenPanel.repaint();
    }    
    
    public synchronized void toggleStartGamePanel(boolean b) {
    	startGameButton.setEnabled(b);
    	startGamePanel.setEnabled(b);
    	startGamePanel.repaint();
    }
    
    public synchronized void setChooseTokenComboBox(List<String> options) {
    	chooseTokenComboBox.removeAllItems();
    	for(String s : options) {
    		chooseTokenComboBox.addItem(s);
    	}
    }
    
    public synchronized void setMoveDirectionButtons(List<MoveDirection> directions) {
    	for(MoveDirection md : directions) {
    		switch(md) {
    		case NORTH:
    			northButton.setEnabled(true);
    			break;
    		case SOUTH:
    			southButton.setEnabled(true);
    			break;
    		case WEST:
    			westButton.setEnabled(true);
    			break;
    		case EAST:
    			eastButton.setEnabled(true);
    			break;
    		case SECRET:
    			secretButton.setEnabled(true);
    			break;
			default:
    				break;
    		}
    	}
    }
    
    public synchronized void setSuggestionComboBox(List<String> tokens, List<String> weapons) {
    	suggestTokenComboBox.removeAllItems();
    	for(String t : tokens) {
    		suggestTokenComboBox.addItem(t);
    	}
    	suggestWeaponComboBox.removeAllItems();
    	for(String w : weapons) {
    		suggestWeaponComboBox.addItem(w);
    	}
    }
    
    public synchronized void setAccusationComboBox(List<String> rooms, List<String> tokens, List<String> weapons) {
    	accuseRoomComboBox.removeAllItems();
    	for(String r : rooms) {
    		accuseRoomComboBox.addItem(r);
    	}
    	accuseTokenComboBox.removeAllItems();
    	for(String t : tokens) {
    		accuseTokenComboBox.addItem(t);
    	}accuseWeaponComboBox.removeAllItems();
    	for(String w : weapons) {
    		accuseWeaponComboBox.addItem(w);
    	}
    }
    
    public synchronized void setShowableCards(List<Integer> cardTypes, List<Integer> cardIds) {
    	cardComboBox.removeAllItems();
    	for(int i=0; i<cardTypes.size(); i++) {
    		CardType cardType = Card.getCardTypeByValue(cardTypes.get(i));
    		String displayString = null;
    		if(cardType == CardType.ROOM) {
    			RoomId roomId = Room.getRoomIdByValue(cardIds.get(i));
    			displayString = roomId.getDefaultName() + " (" + cardType.toString() + ")";
    		}else if(cardType == CardType.TOKEN) {
    			TokenId tokenId = Token.getTokenIdByValue(cardIds.get(i));
    			displayString = tokenId.getDefaultName() + " (" + cardType.toString() + ")";
    		}else {
    			WeaponId weaponId = Weapon.getWeaponIdByValue(cardIds.get(i));
    			displayString = weaponId.getDefaultName() + " (" + cardType.toString() + ")";    			
    		}
    		cardComboBox.addItem(displayString);
    	}
	}
    
    public synchronized void printMessage(String message) {
    	appendToPane(messageHistoryTextPane,message+"\n",Color.BLACK);
    	messageHistoryTextPane.repaint();
    }
    
    public synchronized void printErrorMessage(String message) {
    	appendToPane(messageHistoryTextPane,message+"\n",Color.RED);
    	messageHistoryTextPane.repaint();
    }
    
    public synchronized void updateBoardTextPane(String message) {
    	boardTextPane.setText("");
    	appendToPane(boardTextPane,message,Color.BLUE);
    	boardTextPane.repaint();
    }
    
    private void appendToPane(JTextPane tp, String msg, Color c)
    {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = tp.getDocument().getLength();
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(aset, false);
        tp.replaceSelection(msg);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menuPanel = new javax.swing.JPanel();
        connectionPanel = new javax.swing.JPanel();
        ipAddressLabel = new javax.swing.JLabel();
        ipAddressTextField = new javax.swing.JTextField();
        portLabel = new javax.swing.JLabel();
        portTextField = new javax.swing.JTextField();
        connectButton = new javax.swing.JButton();
        joinGamePanel = new javax.swing.JPanel();
        playerIdLabel = new javax.swing.JLabel();
        playerIdTextField = new javax.swing.JTextField();
        joinGameButton = new javax.swing.JButton();
        movePanel = new javax.swing.JPanel();
        northButton = new javax.swing.JButton();
        secretButton = new javax.swing.JButton();
        southButton = new javax.swing.JButton();
        westButton = new javax.swing.JButton();
        eastButton = new javax.swing.JButton();
        suggestPanel = new javax.swing.JPanel();
        suggestTokenLabel = new javax.swing.JLabel();
        suggestWeaponLabel = new javax.swing.JLabel();
        suggestTokenComboBox = new javax.swing.JComboBox<>();
        suggestWeaponComboBox = new javax.swing.JComboBox<>();
        suggestButton = new javax.swing.JButton();
        accusePanel = new javax.swing.JPanel();
        accuseRoomLabel = new javax.swing.JLabel();
        accuseTokenLabel = new javax.swing.JLabel();
        accuseWeaponLabel = new javax.swing.JLabel();
        accuseRoomComboBox = new javax.swing.JComboBox<>();
        accuseTokenComboBox = new javax.swing.JComboBox<>();
        accuseWeaponComboBox = new javax.swing.JComboBox<>();
        accuseButton = new javax.swing.JButton();
        endTurnPanel = new javax.swing.JPanel();
        endTurnButton = new javax.swing.JButton();
        showCardPanel = new javax.swing.JPanel();
        cardLabel = new javax.swing.JLabel();
        cardComboBox = new javax.swing.JComboBox<>();
        showCardButton = new javax.swing.JButton();
        chooseTokenPanel = new javax.swing.JPanel();
        chooseTokenLabel = new javax.swing.JLabel();
        chooseTokenComboBox = new javax.swing.JComboBox<>();
        chooseTokenButton = new javax.swing.JButton();
        discardTokenPanel = new javax.swing.JPanel();
        discardTokenButton = new javax.swing.JButton();
        startGamePanel = new javax.swing.JPanel();
        startGameButton = new javax.swing.JButton();
        informationPanel = new javax.swing.JPanel();
        messageHistoryPanel = new javax.swing.JPanel();
        messageHistoryScrollPane = new javax.swing.JScrollPane();
        messageHistoryTextPane = new javax.swing.JTextPane();
        boardPanel = new javax.swing.JPanel();
        boardScrollPane = new javax.swing.JScrollPane();
        boardTextPane = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        connectionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Server Connection"));

        ipAddressLabel.setText("IP Address:");

        ipAddressTextField.setText("localhost");
        ipAddressTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ipAddressTextFieldActionPerformed(evt);
            }
        });

        portLabel.setText("Port:");

        portTextField.setText("2017");

        connectButton.setText("Connect to Server");
        connectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout connectionPanelLayout = new javax.swing.GroupLayout(connectionPanel);
        connectionPanel.setLayout(connectionPanelLayout);
        connectionPanelLayout.setHorizontalGroup(
            connectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(connectionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(connectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(connectButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(connectionPanelLayout.createSequentialGroup()
                        .addGroup(connectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ipAddressLabel)
                            .addComponent(portLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(connectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ipAddressTextField)
                            .addComponent(portTextField))))
                .addContainerGap())
        );
        connectionPanelLayout.setVerticalGroup(
            connectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(connectionPanelLayout.createSequentialGroup()
                .addGroup(connectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ipAddressLabel)
                    .addComponent(ipAddressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(connectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(portLabel)
                    .addComponent(portTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(connectButton))
        );

        joinGamePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Join Game"));

        playerIdLabel.setText("Player ID:");

        joinGameButton.setText("Join Game");
        joinGameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                joinGameButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout joinGamePanelLayout = new javax.swing.GroupLayout(joinGamePanel);
        joinGamePanel.setLayout(joinGamePanelLayout);
        joinGamePanelLayout.setHorizontalGroup(
            joinGamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(joinGamePanelLayout.createSequentialGroup()
                .addGroup(joinGamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(joinGamePanelLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(playerIdLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(playerIdTextField))
                    .addGroup(joinGamePanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(joinGameButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        joinGamePanelLayout.setVerticalGroup(
            joinGamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(joinGamePanelLayout.createSequentialGroup()
                .addGroup(joinGamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(playerIdLabel)
                    .addComponent(playerIdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(joinGameButton))
        );

        movePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Move"));

        northButton.setText("North");
        northButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                northButtonActionPerformed(evt);
            }
        });

        secretButton.setText("Secret");
        secretButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                secretButtonActionPerformed(evt);
            }
        });

        southButton.setText("South");
        southButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                southButtonActionPerformed(evt);
            }
        });

        westButton.setText("West");
        westButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                westButtonActionPerformed(evt);
            }
        });

        eastButton.setText("East");
        eastButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eastButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout movePanelLayout = new javax.swing.GroupLayout(movePanel);
        movePanel.setLayout(movePanelLayout);
        movePanelLayout.setHorizontalGroup(
            movePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(movePanelLayout.createSequentialGroup()
                .addComponent(westButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(movePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(northButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(secretButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(southButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(eastButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        movePanelLayout.setVerticalGroup(
            movePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(movePanelLayout.createSequentialGroup()
                .addComponent(northButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(movePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(secretButton)
                    .addComponent(westButton)
                    .addComponent(eastButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(southButton))
        );

        suggestPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Suggest"));

        suggestTokenLabel.setText("Token:");

        suggestWeaponLabel.setText("Weapon:");

        suggestButton.setText("Make Suggestion");
        suggestButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                suggestButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout suggestPanelLayout = new javax.swing.GroupLayout(suggestPanel);
        suggestPanel.setLayout(suggestPanelLayout);
        suggestPanelLayout.setHorizontalGroup(
            suggestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(suggestPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(suggestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(suggestButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(suggestPanelLayout.createSequentialGroup()
                        .addGroup(suggestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(suggestTokenLabel)
                            .addComponent(suggestWeaponLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(suggestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(suggestWeaponComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(suggestTokenComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        suggestPanelLayout.setVerticalGroup(
            suggestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(suggestPanelLayout.createSequentialGroup()
                .addGroup(suggestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(suggestTokenLabel)
                    .addComponent(suggestTokenComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(suggestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(suggestWeaponLabel)
                    .addComponent(suggestWeaponComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(suggestButton))
        );

        accusePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Accuse"));

        accuseRoomLabel.setText("Room:");

        accuseTokenLabel.setText("Token:");

        accuseWeaponLabel.setText("Weapon:");

        accuseButton.setText("Make Accusation");
        accuseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                accuseButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout accusePanelLayout = new javax.swing.GroupLayout(accusePanel);
        accusePanel.setLayout(accusePanelLayout);
        accusePanelLayout.setHorizontalGroup(
            accusePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(accusePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(accusePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(accuseButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(accusePanelLayout.createSequentialGroup()
                        .addGroup(accusePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(accuseTokenLabel)
                            .addComponent(accuseRoomLabel)
                            .addComponent(accuseWeaponLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(accusePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(accuseWeaponComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(accuseTokenComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(accuseRoomComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        accusePanelLayout.setVerticalGroup(
            accusePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(accusePanelLayout.createSequentialGroup()
                .addGroup(accusePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(accuseRoomLabel)
                    .addComponent(accuseRoomComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(accusePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(accuseTokenLabel)
                    .addComponent(accuseTokenComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(accusePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(accuseWeaponLabel)
                    .addComponent(accuseWeaponComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(accuseButton))
        );

        endTurnPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("End Turn"));

        endTurnButton.setText("End Turn");
        endTurnButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endTurnButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout endTurnPanelLayout = new javax.swing.GroupLayout(endTurnPanel);
        endTurnPanel.setLayout(endTurnPanelLayout);
        endTurnPanelLayout.setHorizontalGroup(
            endTurnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(endTurnPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(endTurnButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        endTurnPanelLayout.setVerticalGroup(
            endTurnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(endTurnButton)
        );

        showCardPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Show Card"));

        cardLabel.setText("Card:");

        showCardButton.setText("Show Card");
        showCardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showCardButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout showCardPanelLayout = new javax.swing.GroupLayout(showCardPanel);
        showCardPanel.setLayout(showCardPanelLayout);
        showCardPanelLayout.setHorizontalGroup(
            showCardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(showCardPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(showCardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(showCardButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(showCardPanelLayout.createSequentialGroup()
                        .addComponent(cardLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cardComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        showCardPanelLayout.setVerticalGroup(
            showCardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(showCardPanelLayout.createSequentialGroup()
                .addGroup(showCardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cardLabel)
                    .addComponent(cardComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(showCardButton))
        );

        chooseTokenPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Choose Token"));

        chooseTokenLabel.setText("Token:");

        chooseTokenButton.setText("Choose Token");
        chooseTokenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseTokenButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout chooseTokenPanelLayout = new javax.swing.GroupLayout(chooseTokenPanel);
        chooseTokenPanel.setLayout(chooseTokenPanelLayout);
        chooseTokenPanelLayout.setHorizontalGroup(
            chooseTokenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chooseTokenPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(chooseTokenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chooseTokenButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(chooseTokenPanelLayout.createSequentialGroup()
                        .addComponent(chooseTokenLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chooseTokenComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        chooseTokenPanelLayout.setVerticalGroup(
            chooseTokenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chooseTokenPanelLayout.createSequentialGroup()
                .addGroup(chooseTokenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chooseTokenLabel)
                    .addComponent(chooseTokenComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chooseTokenButton))
        );

        discardTokenPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Discard Token"));

        discardTokenButton.setText("Discard Token");
        discardTokenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                discardTokenButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout discardTokenPanelLayout = new javax.swing.GroupLayout(discardTokenPanel);
        discardTokenPanel.setLayout(discardTokenPanelLayout);
        discardTokenPanelLayout.setHorizontalGroup(
            discardTokenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(discardTokenPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(discardTokenButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        discardTokenPanelLayout.setVerticalGroup(
            discardTokenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(discardTokenPanelLayout.createSequentialGroup()
                .addComponent(discardTokenButton)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        startGamePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Start Game"));

        startGameButton.setText("Vote");
        startGameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startGameButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout startGamePanelLayout = new javax.swing.GroupLayout(startGamePanel);
        startGamePanel.setLayout(startGamePanelLayout);
        startGamePanelLayout.setHorizontalGroup(
            startGamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(startGamePanelLayout.createSequentialGroup()
                .addComponent(startGameButton)
                .addGap(0, 10, Short.MAX_VALUE))
        );
        startGamePanelLayout.setVerticalGroup(
            startGamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(startGamePanelLayout.createSequentialGroup()
                .addComponent(startGameButton)
                .addGap(0, 11, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout menuPanelLayout = new javax.swing.GroupLayout(menuPanel);
        menuPanel.setLayout(menuPanelLayout);
        menuPanelLayout.setHorizontalGroup(
            menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuPanelLayout.createSequentialGroup()
                .addGroup(menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(endTurnPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(accusePanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(connectionPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(joinGamePanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chooseTokenPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(suggestPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(movePanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(showCardPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, menuPanelLayout.createSequentialGroup()
                        .addComponent(discardTokenPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(startGamePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 10, Short.MAX_VALUE))
        );
        menuPanelLayout.setVerticalGroup(
            menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuPanelLayout.createSequentialGroup()
                .addComponent(connectionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(joinGamePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chooseTokenPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(discardTokenPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(menuPanelLayout.createSequentialGroup()
                        .addComponent(startGamePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showCardPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(movePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(suggestPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(accusePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(endTurnPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        messageHistoryPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Message History"));

        messageHistoryScrollPane.setViewportView(messageHistoryTextPane);

        javax.swing.GroupLayout messageHistoryPanelLayout = new javax.swing.GroupLayout(messageHistoryPanel);
        messageHistoryPanel.setLayout(messageHistoryPanelLayout);
        messageHistoryPanelLayout.setHorizontalGroup(
            messageHistoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(messageHistoryScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 667, Short.MAX_VALUE)
        );
        messageHistoryPanelLayout.setVerticalGroup(
            messageHistoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(messageHistoryScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout informationPanelLayout = new javax.swing.GroupLayout(informationPanel);
        informationPanel.setLayout(informationPanelLayout);
        informationPanelLayout.setHorizontalGroup(
            informationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, informationPanelLayout.createSequentialGroup()
                .addComponent(messageHistoryPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        informationPanelLayout.setVerticalGroup(
            informationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, informationPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(messageHistoryPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        boardScrollPane.setViewportView(boardTextPane);

        javax.swing.GroupLayout boardPanelLayout = new javax.swing.GroupLayout(boardPanel);
        boardPanel.setLayout(boardPanelLayout);
        boardPanelLayout.setHorizontalGroup(
            boardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(boardPanelLayout.createSequentialGroup()
                .addComponent(boardScrollPane)
                .addContainerGap())
        );
        boardPanelLayout.setVerticalGroup(
            boardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, boardPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(boardScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 506, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(menuPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 3, Short.MAX_VALUE)
                        .addComponent(informationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(boardPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(menuPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(boardPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(informationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ipAddressTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ipAddressTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ipAddressTextFieldActionPerformed

    private void connectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectButtonActionPerformed
        // Get text from the ipAddress field
        String ipAddress = ipAddressTextField.getText();
        int port = -1;
        try{
            port = Integer.parseInt(portTextField.getText());
        }catch(NumberFormatException e){
            JOptionPane.showMessageDialog(null, "Invalid port number", "Error",
                                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        disableAllMenuPanels();       
        graphicalUI.connectToServer(ipAddress, port);
    }//GEN-LAST:event_connectButtonActionPerformed

    private void southButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_southButtonActionPerformed
    	PlayerAction pa = new PlayerActionMove(
    			graphicalUI.getPlayerId(),
    			MoveDirection.SOUTH);
    	graphicalUI.gameClient.sendPlayerAction(pa); 
    }//GEN-LAST:event_southButtonActionPerformed

    private void joinGameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_joinGameButtonActionPerformed
    	String playerId = playerIdTextField.getText();
    	if(playerId.length() == 0) {
            JOptionPane.showMessageDialog(null, "Player ID cannot be empty", "Error",
                    JOptionPane.ERROR_MESSAGE);
        	return;
    	}
    	disableAllMenuPanels();
    	graphicalUI.joinGame(playerId);
    }//GEN-LAST:event_joinGameButtonActionPerformed

    private void chooseTokenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chooseTokenButtonActionPerformed
    	String token = (String) chooseTokenComboBox.getSelectedItem();
    	if(token == null) {
            JOptionPane.showMessageDialog(null, "Invalid token selection", "Error",
                    JOptionPane.ERROR_MESSAGE);
        	return;    		
    	}
    	PlayerAction pa = new PlayerActionChooseToken(
    			graphicalUI.getPlayerId(),
    			token);
    	graphicalUI.gameClient.sendPlayerAction(pa);
    }//GEN-LAST:event_chooseTokenButtonActionPerformed

    private void discardTokenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_discardTokenButtonActionPerformed
    	PlayerAction pa = new PlayerActionDiscardToken(
    			graphicalUI.getPlayerId());
    	graphicalUI.gameClient.sendPlayerAction(pa); 	
    }//GEN-LAST:event_discardTokenButtonActionPerformed

    private void startGameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startGameButtonActionPerformed
    	PlayerAction pa = new PlayerActionVoteStartGame(
    			graphicalUI.getPlayerId());
    	graphicalUI.gameClient.sendPlayerAction(pa); 	
    }//GEN-LAST:event_startGameButtonActionPerformed

    private void northButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_northButtonActionPerformed
    	PlayerAction pa = new PlayerActionMove(
    			graphicalUI.getPlayerId(),
    			MoveDirection.NORTH);
    	graphicalUI.gameClient.sendPlayerAction(pa); 
    }//GEN-LAST:event_northButtonActionPerformed

    private void secretButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_secretButtonActionPerformed
    	PlayerAction pa = new PlayerActionMove(
    			graphicalUI.getPlayerId(),
    			MoveDirection.SECRET);
    	graphicalUI.gameClient.sendPlayerAction(pa); 
    }//GEN-LAST:event_secretButtonActionPerformed

    private void westButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_westButtonActionPerformed
    	PlayerAction pa = new PlayerActionMove(
    			graphicalUI.getPlayerId(),
    			MoveDirection.WEST);
    	graphicalUI.gameClient.sendPlayerAction(pa); 
    }//GEN-LAST:event_westButtonActionPerformed

    private void eastButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eastButtonActionPerformed
    	PlayerAction pa = new PlayerActionMove(
    			graphicalUI.getPlayerId(),
    			MoveDirection.EAST);
    	graphicalUI.gameClient.sendPlayerAction(pa); 
    }//GEN-LAST:event_eastButtonActionPerformed

    private void suggestButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_suggestButtonActionPerformed
    	String token = (String) suggestTokenComboBox.getSelectedItem();
    	if(token == null) {
            JOptionPane.showMessageDialog(null, "Invalid token selection", "Error",
                    JOptionPane.ERROR_MESSAGE);
        	return;    		
    	}
    	String weapon = (String) suggestWeaponComboBox.getSelectedItem();
    	if(weapon == null) {
            JOptionPane.showMessageDialog(null, "Invalid weapon selection", "Error",
                    JOptionPane.ERROR_MESSAGE);
        	return;    		
    	}
    	PlayerAction pa = new PlayerActionMakeSuggestion(
    			graphicalUI.getPlayerId(),
    			token,
    			weapon);
    	graphicalUI.gameClient.sendPlayerAction(pa);
    }//GEN-LAST:event_suggestButtonActionPerformed

    private void accuseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_accuseButtonActionPerformed
    	String room = (String) accuseRoomComboBox.getSelectedItem();
    	if(room == null) {
            JOptionPane.showMessageDialog(null, "Invalid room selection", "Error",
                    JOptionPane.ERROR_MESSAGE);
        	return;    		
    	}
    	String token = (String) accuseTokenComboBox.getSelectedItem();
    	if(token == null) {
            JOptionPane.showMessageDialog(null, "Invalid token selection", "Error",
                    JOptionPane.ERROR_MESSAGE);
        	return;    		
    	}
    	String weapon = (String) accuseWeaponComboBox.getSelectedItem();
    	if(weapon == null) {
            JOptionPane.showMessageDialog(null, "Invalid weapon selection", "Error",
                    JOptionPane.ERROR_MESSAGE);
        	return;    		
    	}
    	PlayerAction pa = new PlayerActionMakeAccusation(
    			graphicalUI.getPlayerId(),
    			room,
    			token,
    			weapon);
    	graphicalUI.gameClient.sendPlayerAction(pa);
    }//GEN-LAST:event_accuseButtonActionPerformed

    private void endTurnButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endTurnButtonActionPerformed
    	PlayerAction pa = new PlayerActionEndTurn(
    			graphicalUI.getPlayerId());
    	graphicalUI.gameClient.sendPlayerAction(pa);
    }//GEN-LAST:event_endTurnButtonActionPerformed

    private void showCardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showCardButtonActionPerformed
    	List<Integer> cardTypes = graphicalUI.gameStateResult.playerShowableCardTypes;
    	List<Integer> cardIds = graphicalUI.gameStateResult.playerShowableCardIds;

    	int selectedIdx = cardComboBox.getSelectedIndex();
    	if(selectedIdx < 0 || selectedIdx >= cardTypes.size()) {
            JOptionPane.showMessageDialog(null, "Invalid card selection", "Error",
                    JOptionPane.ERROR_MESSAGE);
        	return;  
    	}
    	
    	CardType cardType = Card.getCardTypeByValue(cardTypes.get(selectedIdx));
    	String cardIdString = null;
		if(cardType == CardType.ROOM) {
			RoomId roomId = Room.getRoomIdByValue(cardIds.get(selectedIdx));
			cardIdString = roomId.getDefaultName();
		}else if(cardType == CardType.TOKEN) {
			TokenId tokenId = Token.getTokenIdByValue(cardIds.get(selectedIdx));
			cardIdString = tokenId.getDefaultName();
		}else {
			WeaponId weaponId = Weapon.getWeaponIdByValue(cardIds.get(selectedIdx));
			cardIdString = weaponId.getDefaultName();
		}
    	
    	PlayerAction pa = new PlayerActionShowCard(
    			graphicalUI.getPlayerId(),
    			cardType.toString(),
    			cardIdString
    			);
    	graphicalUI.gameClient.sendPlayerAction(pa);
    }//GEN-LAST:event_showCardButtonActionPerformed

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
            java.util.logging.Logger.getLogger(GameJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GameJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GameJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GameJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GameJFrame(null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton accuseButton;
    private javax.swing.JPanel accusePanel;
    private javax.swing.JComboBox<String> accuseRoomComboBox;
    private javax.swing.JLabel accuseRoomLabel;
    private javax.swing.JComboBox<String> accuseTokenComboBox;
    private javax.swing.JLabel accuseTokenLabel;
    private javax.swing.JComboBox<String> accuseWeaponComboBox;
    private javax.swing.JLabel accuseWeaponLabel;
    private javax.swing.JPanel boardPanel;
    private javax.swing.JScrollPane boardScrollPane;
    private javax.swing.JTextPane boardTextPane;
    private javax.swing.JComboBox<String> cardComboBox;
    private javax.swing.JLabel cardLabel;
    private javax.swing.JButton chooseTokenButton;
    private javax.swing.JComboBox<String> chooseTokenComboBox;
    private javax.swing.JLabel chooseTokenLabel;
    private javax.swing.JPanel chooseTokenPanel;
    private javax.swing.JButton connectButton;
    private javax.swing.JPanel connectionPanel;
    private javax.swing.JButton discardTokenButton;
    private javax.swing.JPanel discardTokenPanel;
    private javax.swing.JButton eastButton;
    private javax.swing.JButton endTurnButton;
    private javax.swing.JPanel endTurnPanel;
    private javax.swing.JPanel informationPanel;
    private javax.swing.JLabel ipAddressLabel;
    private javax.swing.JTextField ipAddressTextField;
    private javax.swing.JButton joinGameButton;
    private javax.swing.JPanel joinGamePanel;
    private javax.swing.JPanel menuPanel;
    private javax.swing.JPanel messageHistoryPanel;
    private javax.swing.JScrollPane messageHistoryScrollPane;
    private javax.swing.JTextPane messageHistoryTextPane;
    private javax.swing.JPanel movePanel;
    private javax.swing.JButton northButton;
    private javax.swing.JLabel playerIdLabel;
    private javax.swing.JTextField playerIdTextField;
    private javax.swing.JLabel portLabel;
    private javax.swing.JTextField portTextField;
    private javax.swing.JButton secretButton;
    private javax.swing.JButton showCardButton;
    private javax.swing.JPanel showCardPanel;
    private javax.swing.JButton southButton;
    private javax.swing.JButton startGameButton;
    private javax.swing.JPanel startGamePanel;
    private javax.swing.JButton suggestButton;
    private javax.swing.JPanel suggestPanel;
    private javax.swing.JComboBox<String> suggestTokenComboBox;
    private javax.swing.JLabel suggestTokenLabel;
    private javax.swing.JComboBox<String> suggestWeaponComboBox;
    private javax.swing.JLabel suggestWeaponLabel;
    private javax.swing.JButton westButton;
    // End of variables declaration//GEN-END:variables
}
