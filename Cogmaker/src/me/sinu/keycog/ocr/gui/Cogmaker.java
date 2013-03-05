/*
 * Developer : Sinu John
 * www.sinujohn.wordpress.com
 */

package me.sinu.keycog.ocr.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import me.sinu.keycog.ocr.ImageFile;
import me.sinu.keycog.trainer.Trainer;
import me.sinu.thulika.entity.CharData;
import me.sinu.thulika.entity.ImageData;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class Cogmaker {

	private JFrame frmMaker;
	private JFileChooser fcOpen;
	private JFileChooser fcSave;
	private String letterFilePath=null;
	private Trainer trainer = new Trainer();
	private JTextField widthBox;
	private JTextField heightBox;
	private JTextField langBox;
	private JList imageListBox;
	private JLabel imageLabel;
	private JList charListBox;
	private JTextField symField;
	private JTextField rulesBox;
	private JSpinner alignSelector;
	private JTextField alterBox;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Cogmaker window = new Cogmaker();
					window.frmMaker.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Cogmaker() {
		initialize();
	}
	
	public Image getImageFromArray(int[] pixels, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        //WritableRaster raster = (WritableRaster) image.getData();
        for(int i=0; i<height; i++){
        	for(int j=0; j<width; j++){
        		image.setRGB(j, i, pixels[i*width + j]);
        	}
        }        
        //raster.setPixels(0,0,width,height,pixels);
        return image; 
    }

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmMaker = new JFrame();
		frmMaker.setTitle("ThulikaMaker");
		frmMaker.setBounds(100, 100, 564, 637);
		frmMaker.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		fcOpen = new JFileChooser();
		fcOpen.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fcSave = new JFileChooser();
		fcSave.setFileSelectionMode(JFileChooser.FILES_ONLY);
		frmMaker.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JLayeredPane layeredPane = new JLayeredPane();
		frmMaker.getContentPane().add(layeredPane, BorderLayout.NORTH);
		layeredPane.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		JLabel lblWidth = new JLabel("Width");
		layeredPane.add(lblWidth, "2, 2");
		
		widthBox = new JTextField();
		layeredPane.add(widthBox, "6, 2");
		widthBox.setColumns(10);
		
		JLabel lblHeight = new JLabel("Height");
		layeredPane.add(lblHeight, "2, 4");
		
		heightBox = new JTextField();
		layeredPane.add(heightBox, "6, 4");
		heightBox.setColumns(10);
		
		
		JButton btnLoadBundle = new JButton("Load Bundle");
		layeredPane.add(btnLoadBundle, "6, 6");
		final JFileChooser openBox;
		openBox = new JFileChooser();
		openBox.setFileSelectionMode(JFileChooser.FILES_ONLY);
		btnLoadBundle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int returnVal = fcOpen.showOpenDialog(frmMaker);
				 
	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	                File file = fcOpen.getSelectedFile();
	                letterFilePath = file.getPath();
	                trainer.loadLettersAction(letterFilePath, Integer.parseInt(widthBox.getText()), Integer.parseInt(heightBox.getText()));
	            } else {
	            	//nothing
	            }
	            charListBox.setListData(trainer.getCharSet().keySet().toArray());
	            charListBox.setSelectedIndex(0);
			}
		});
		
		JLayeredPane layeredPane_1 = new JLayeredPane();
		frmMaker.getContentPane().add(layeredPane_1, BorderLayout.SOUTH);
		layeredPane_1.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		JLabel lblLanguageId = new JLabel("Language ID");
		layeredPane_1.add(lblLanguageId, "2, 2");
		
		langBox = new JTextField();
		layeredPane_1.add(langBox, "6, 2");
		langBox.setColumns(10);
		
		JButton btnSaveEngine = new JButton("Save Engine");
		layeredPane_1.add(btnSaveEngine, "6, 4");
		btnSaveEngine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(letterFilePath==null || letterFilePath.isEmpty()) {
					JOptionPane.showMessageDialog(frmMaker,"Please load a file");
				} else {
					int returnVal = fcSave.showSaveDialog(frmMaker);
		            if (returnVal == JFileChooser.APPROVE_OPTION) {
		                File file = fcSave.getSelectedFile();
		                try {
							if(trainer.trainAndSave(file.getPath(), langBox.getText())) {
								JOptionPane.showMessageDialog(frmMaker,"Engine saved!");
							}
						} catch (IOException e) {
							JOptionPane.showMessageDialog(frmMaker,e.getMessage());
							e.printStackTrace();
						}
		            } else {
		                //nothing
		            }
				}
			}
		});
		
		JLayeredPane layeredPane_2 = new JLayeredPane();
		frmMaker.getContentPane().add(layeredPane_2);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(165, 11, 102, 150);
		layeredPane_2.add(scrollPane);
		
		imageListBox = new JList();
		imageListBox.setFont(new Font("Kartika", Font.PLAIN, 11));
		imageListBox.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				//try {
					ImageFile iFile = (ImageFile) imageListBox.getSelectedValue();
					
					if(iFile==null) {
						imageListBox.setSelectedIndex(0);
						iFile = (ImageFile) imageListBox.getSelectedValue();
						if(iFile==null) {
							return;
						}
					}
					ImageData iData = iFile.getImageData();
					imageLabel.setIcon(new ImageIcon(getImageFromArray(iData.getPixels(), iData.getWidth(), iData.getHeight())));
					imageLabel.setBounds(0, 0, iData.getWidth(), iData.getHeight());
				/*} catch (Exception e) {
					// ignore
				}*/
			}
		});
		imageListBox.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(imageListBox);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(277, 11, 261, 259);
		layeredPane_2.add(scrollPane_1);
		
		imageLabel = new JLabel("Image");
		scrollPane_1.setViewportView(imageLabel);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(9, 11, 102, 259);
		layeredPane_2.add(scrollPane_2);
		
		charListBox = new JList();
		scrollPane_2.setViewportView(charListBox);
		charListBox.setFont(new Font("Kartika", Font.PLAIN, 11));
		
		JLayeredPane layeredPane_3 = new JLayeredPane();
		layeredPane_3.setBounds(10, 295, 486, 142);
		layeredPane_2.add(layeredPane_3);
		
		JLabel lblSymbol = new JLabel("Symbol");
		lblSymbol.setBounds(10, 11, 46, 14);
		layeredPane_3.add(lblSymbol);
		
		JLabel lblAlign = new JLabel("Align");
		lblAlign.setBounds(10, 39, 46, 14);
		layeredPane_3.add(lblAlign);
		
		JLabel lblRules = new JLabel("Rules");
		lblRules.setBounds(10, 66, 46, 14);
		layeredPane_3.add(lblRules);
		
		symField = new JTextField();
		symField.setEditable(false);
		symField.setFont(new Font("Kartika", Font.PLAIN, 12));
		symField.setBounds(81, 5, 86, 20);
		layeredPane_3.add(symField);
		symField.setColumns(10);
		
		alignSelector = new JSpinner();
		alignSelector.setModel(new SpinnerNumberModel(0, -1, 1, 1));
		alignSelector.setBounds(81, 36, 29, 20);
		layeredPane_3.add(alignSelector);
		
		rulesBox = new JTextField();
		rulesBox.setFont(new Font("Kartika", Font.PLAIN, 12));
		rulesBox.setBounds(81, 64, 381, 20);
		layeredPane_3.add(rulesBox);
		rulesBox.setColumns(10);
		
		JButton btnSaveSymbol = new JButton("Save Symbol");
		btnSaveSymbol.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				CharData currentChar = (CharData) charListBox.getSelectedValue();
				currentChar.setAlign((Integer) alignSelector.getValue());
				Map<String, String> rules = getRules(rulesBox.getText());
				if(rules!=null) {
					currentChar.setMergeRules(rules);
				}
				String alterStr = alterBox.getText();
					//String msg = setAlternativesList(currentChar, alterStr);
					//currentChar.setAlternatives(getAlternativesList(currentChar, alterStr));
					/*if(msg!=null && !msg.isEmpty()) {
						JOptionPane.showMessageDialog(frmMaker,"Characters "+msg +" cannot be found");
					}*/
			}
		});
		btnSaveSymbol.setBounds(81, 95, 106, 23);
		layeredPane_3.add(btnSaveSymbol);
		
		JLabel lblAlternates = new JLabel("Alternates");
		lblAlternates.setBounds(234, 11, 60, 14);
		layeredPane_3.add(lblAlternates);
		
		alterBox = new JTextField();
		alterBox.setFont(new Font("Kartika", Font.PLAIN, 11));
		alterBox.setBounds(304, 8, 158, 20);
		layeredPane_3.add(alterBox);
		alterBox.setColumns(10);
		
		JButton btnDeleteFile = new JButton("Delete File");
		btnDeleteFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String filename = ((ImageFile)imageListBox.getSelectedValue()).getFilename();
				if(trainer.deleteFile(filename)) {
					JOptionPane.showMessageDialog(frmMaker,"File "+filename +" is deleted");
				}
				try {
					populateImageData();
				} catch (Exception e) {
					imageListBox.clearSelection();
					charListBox.setSelectedIndex(0);
					charListBox.setListData(trainer.getCharSet().keySet().toArray());
				}
			}
		});
		btnDeleteFile.setBounds(165, 196, 89, 23);
		layeredPane_2.add(btnDeleteFile);
		charListBox.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				populateImageData();
			}
		});
	}
	
	/*private void copyCharData(File file) {
		ListModel dataModel = charListBox.getModel();
		List<CharData> currentChars = new ArrayList<CharData>(dataModel.getSize());
		for(int i=0; i<dataModel.getSize(); i++) {
			currentChars.add((CharData)dataModel.getElementAt(i));
		}
		String msg="";
		
		try{
			  FileInputStream fstream = new FileInputStream(file);
			  DataInputStream in = new DataInputStream(fstream);
			  BufferedReader br = new BufferedReader(new InputStreamReader(in));
			  String str;
			  while ((str = br.readLine()) != null)   {
				  if(!str.isEmpty()) {
					  try {
						  String symbol = str.substring(str.indexOf(":")+1, str.indexOf("align:")).trim();
						  str = str.substring(str.indexOf("align:"));
						  String align = str.substring(str.indexOf(":")+1, str.indexOf("alter:")).trim();
						  str = str.substring(str.indexOf("alter:"));
						  String alter = str.substring(str.indexOf(":")+1, str.indexOf("rules:")).trim();
						  str = str.substring(str.indexOf("rules:"));
						  String rules;
						  try {
							  rules = str.substring(str.indexOf(":")+1).trim();
						  } catch (Exception e) {
							rules = "";
						  }
						  
						  for(CharData c : currentChars) {
							  if(c.getSymbol().equals(symbol)) {
								  int a;
								  try {
									  a = Integer.parseInt(align);
								  } catch (Exception e) {
									a = 0;
								}
								  c.setAlign(a);
								  String m = setAlternativesList(c, alter);
								  if(!m.isEmpty()) {
									  msg = msg + "Alternatives(" + c.getSymbol() + ") : " + m + " cannot be set\n";
								  }
								  c.setMergeRules(getRules(rules));
							  }
						  }
						  
					  } catch (Exception e) {
						  msg = msg + "ERROR: " + e.getMessage() +"\n";
						  e.printStackTrace();
					}
				  }
			  }
			  //Close the input stream
			  in.close();
		}catch (Exception e){
			  System.err.println("Error: " + e.getMessage());
		}
	}*/

	private void populateImageData() {
		CharData currentChar = (CharData) charListBox.getSelectedValue();
		if(currentChar==null) {
			imageListBox.setListData(new Object[0]);
			symField.setText("");
			alignSelector.setValue(0);
			rulesBox.setText("");
			alterBox.setText("");
			return;
		}
		imageListBox.setListData(trainer.getCharSet().get(currentChar).keySet().toArray());
		imageListBox.setSelectedIndex(0);
		
		symField.setText(currentChar.toString());
		alignSelector.setValue(currentChar.getAlign());
		rulesBox.setText(makeRuleString(currentChar.getMergeRules()));
		//alterBox.setText(makeAlternativesString(currentChar.getAlternatives()));
	}
	
	private Map<String, String> getRules(String ruleString) {
		Map<String, String> ret = new HashMap<String, String>();
		String[] rules = ruleString.split(";");
		for(String rule : rules) {
			if(!rule.isEmpty()) {
				String[] parts = rule.split(":");
				if(parts.length!=2) {
					return null;
				} else {
					String pre = parts[0].trim();
					String result = parts[1].trim();
					ret.put(pre, result);
				}
			}
		}
		return ret;
	}
	
	private String makeRuleString(Map<String, String> ruleMap) {
		String ret="";
		for(String pre : ruleMap.keySet()) {
			String result = ruleMap.get(pre);
			ret = ret+ pre + ":" + result + ";";
		}
		return ret;
	}
	
	/*private String makeAlternativesString(List<CharData> alterList) {
		if(alterList==null) {
			return "";
		}
		String ret="";
		for(CharData str : alterList) {
			ret = ret + str.getSymbol() + ":";
		}
		return ret;
	}
	
	private  String setAlternativesList(CharData current, String alterStr) {
		String[] strArray = alterStr.split(":");
		ArrayList<CharData> ret = new ArrayList<CharData>(strArray.length);
		ListModel dataModel = charListBox.getModel();
		List<CharData> currentChars = new ArrayList<CharData>(dataModel.getSize());
		for(int i=0; i<dataModel.getSize(); i++) {
			currentChars.add((CharData)dataModel.getElementAt(i));
		}
		
		String wrongMsg = "";
		
		
			
		for(String str : strArray) {
			if(!str.isEmpty()) {
				boolean found = false;
				for(CharData c : currentChars) {
					if(c!=current) {
						if(c.getSymbol().equals(str)) {
							found = true;
							ret.add(c);
							if(c.getAlternatives()!=null && !c.getAlternatives().contains(current)) {
								c.getAlternatives().add(current);
							} else if(c.getAlternatives()==null) {
								List<CharData> l = new ArrayList<CharData>(1);
								l.add(current);
								c.setAlternatives(l);
							}
							break;
						}
					}
				}
				if(!found) {
					wrongMsg = wrongMsg + ", " + str;
				}
			}
		}
		
		if(ret.isEmpty()) {
			ret = null;
		}
		
		for(CharData c : currentChars) {
			List<CharData> l = c.getAlternatives();
			if(l!=null) {
				for(CharData d : currentChars) {
					if(c!=d && d.getAlternatives()!=null) {
						if(!l.contains(d) && d.getAlternatives().contains(c)) {
							d.getAlternatives().remove(c);
							if(d.getAlternatives().isEmpty()) {
								d.setAlternatives(null);
							}
						}
					}
				}
			}
		}
		
		current.setAlternatives(ret);
		return wrongMsg;
		//return ret;
	}*/
}
