/* On my honor, I have neither given nor received unauthorized aid on this assignment */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;


public class MIPSsim {
	HashMap<String, String> category1 = new HashMap<String, String>();
	HashMap<String, String> category2 = new HashMap<String, String>();
	HashMap<String, String> category3 = new HashMap<String, String>();
	List<HashMap<String, Integer>> ins = new ArrayList<HashMap<String,Integer>>();
	List<String> insList = new ArrayList<String>();
	List<String> insNameList = new ArrayList<String>();
	List<Integer> preIssue = new ArrayList<Integer>();
	List<Integer> preALU = new ArrayList<Integer>();
	List<Integer> tempIssueIntegers = new ArrayList<Integer>();
	int postALU = -1;
	int preMEM = -1;
	int postMEM = -1;
	int fetch1 = -1;
	int fetch2 = -1;
	int waiting = -1;
	int executed = -1;
	int IssuedNum = 0;
	int aluIns = -1;
	int temppostALU = -1;
	int tempPreMem = -1;
	int tempPostMem = -1;
	static final int basePC = 128; 
	static int pc = 128;
	HashMap<Integer, Integer> memories = new HashMap<Integer, Integer>();
	int[] gpr = new int[32];
	public MIPSsim() {
		init();
	}
	public void parse(){
		BufferedReader br = null;
		BufferedWriter bw = null;
		String newLine = "\r";
		String os = System.getProperty("os.name").toLowerCase();
		if(os.contains("linux"))
			newLine = "\n";
		try{
		    Scanner sc=new Scanner(System.in);
		    System.out.println("input the file address£º");
		    String m=sc.next();
			br = new BufferedReader(new FileReader(m));
			OutputStreamWriter writer = null;
			String line = null;
			boolean end = false;
			String writeLine = "";
			while((line = br.readLine()) != null){
				writeLine +=line +"\t"+ pc +"\t";
				String substring = line.substring(0, 3);
					if(substring.equals("000")){
						String insName = category1.get(line.substring(3, 6));
						if(insName.equals("J")&&!end){
							writeLine += insName + " #"+Integer.parseInt(line.substring(6, 32)+"00", 2)+newLine;
							HashMap<String, Integer> desc = new HashMap<String, Integer>();
							desc.put("address", Integer.parseInt(line.substring(6, 32)+"00", 2));
							desc.put("pc", pc);
							desc.put("category", 1);
							ins.add(desc);
							insList.add(insName + " #"+Integer.parseInt(line.substring(6, 32)+"00", 2));
							insNameList.add(insName);
						}
						else if(insName.equals("BEQ")&&!end){
							writeLine += insName +" R"+Integer.parseInt(line.substring(6,11),2)+
									", R"+Integer.parseInt(line.substring(11,16),2)
									+", #"+Integer.parseInt(line.substring(16, 32), 2)*4+newLine;
							HashMap<String, Integer> desc = new HashMap<String, Integer>();
							desc.put("rs", Integer.parseInt(line.substring(6, 11), 2));
							desc.put("rt", Integer.parseInt(line.substring(11, 16), 2));
							desc.put("offset", Integer.parseInt(line.substring(16, 32), 2)*4);
							desc.put("pc", pc);
							desc.put("category", 1);
							ins.add(desc);
							insList.add(insName +" R"+Integer.parseInt(line.substring(6,11),2)+
									", R"+Integer.parseInt(line.substring(11,16),2)
									+", #"+Integer.parseInt(line.substring(16, 32), 2)*4);
							insNameList.add(insName);
						}
						else if(insName.equals("BGTZ")&&!end){
							writeLine += insName +" R"+Integer.parseInt(line.substring(6,11),2)+
									", #"+Integer.parseInt(line.substring(16, 32), 2)*4+newLine;
							HashMap<String, Integer> desc = new HashMap<String, Integer>();
							desc.put("rs", Integer.parseInt(line.substring(6, 11), 2));
							desc.put("rt", Integer.parseInt(line.substring(11, 16), 2));
							desc.put("offset", Integer.parseInt(line.substring(16, 32), 2)*4);
							desc.put("pc", pc);
							desc.put("category", 1);
							ins.add(desc);
							insList.add(insName +" R"+Integer.parseInt(line.substring(6,11),2)+
									", #"+Integer.parseInt(line.substring(16, 32), 2)*4);
							insNameList.add(insName);
						}
						else if(insName.equals("BREAK")&&!end){
							writeLine += insName+newLine;
							end = true;
							HashMap<String, Integer> desc = new HashMap<String, Integer>();
							desc.put("pc", pc);
							desc.put("category", 1);
							ins.add(desc);
							insList.add(insName);
							insNameList.add(insName);
						}else if((insName.equals("SW")||insName.equals("LW"))&&!end){
							writeLine += insName +" R"+Integer.parseInt(line.substring(11,16),2)+
									", "+Integer.parseInt(line.substring(16, 32), 2)+"(R"
									+Integer.parseInt(line.substring(6,11),2)+")"+newLine;
							HashMap<String, Integer> desc = new HashMap<String, Integer>();
							desc.put("base", Integer.parseInt(line.substring(6, 11), 2));
							desc.put("rt", Integer.parseInt(line.substring(11, 16), 2));
							desc.put("offset", Integer.parseInt(line.substring(16, 32), 2));
							desc.put("pc", pc);
							desc.put("category", 1);
							ins.add(desc);
							insList.add(insName +" R"+Integer.parseInt(line.substring(11,16),2)+
									", "+Integer.parseInt(line.substring(16, 32), 2)+"(R"
									+Integer.parseInt(line.substring(6,11),2)+")");
							insNameList.add(insName);
						}else {
							writeLine += Integer.parseInt(line, 2)+newLine;
							memories.put(pc, Integer.parseInt(line, 2));
						}
					}else if(substring.equals("110")&&!end){
						String insName = category2.get(line.substring(13,16));
						writeLine += insName+" R"+Integer.parseInt(line.substring(16,21),2)+
								", R"+Integer.parseInt(line.substring(3,8),2)+
								", R"+Integer.parseInt(line.substring(8,13),2)+newLine;
						HashMap<String, Integer> desc = new HashMap<String, Integer>();
						desc.put("rs", Integer.parseInt(line.substring(3, 8), 2));
						desc.put("rt", Integer.parseInt(line.substring(8, 13), 2));
						desc.put("rd", Integer.parseInt(line.substring(16, 21), 2));
						desc.put("category", 2);
						desc.put("pc", pc);
						ins.add(desc);
						insList.add(insName+" R"+Integer.parseInt(line.substring(16,21),2)+
								", R"+Integer.parseInt(line.substring(3,8),2)+
								", R"+Integer.parseInt(line.substring(8,13),2));
						insNameList.add(insName);
					}else if(substring.equals("111")){
						String insName = category3.get(line.substring(13, 16));
						if(insName==null&&end){
							writeLine += -1*(Integer.parseInt(Encode(line.substring(0,32)), 2)+1)+newLine;
							memories.put(pc, -1*(Integer.parseInt(Encode(line.substring(0,32)), 2)+1));
						}else{
							writeLine += insName +" R"+Integer.parseInt(line.substring(8,13),2)+
								", R"+Integer.parseInt(line.substring(3,8),2)
								+", #"+Integer.parseInt(line.substring(16, 32), 2)+newLine;
							HashMap<String, Integer> desc = new HashMap<String, Integer>();
							desc.put("rs", Integer.parseInt(line.substring(3, 8), 2));
							desc.put("rt", Integer.parseInt(line.substring(8, 13), 2));
							desc.put("imme", Integer.parseInt(line.substring(16, 32), 2));
							desc.put("pc", pc);
							desc.put("category", 3);
							ins.add(desc);
							insList.add(insName +" R"+Integer.parseInt(line.substring(8,13),2)+
									", R"+Integer.parseInt(line.substring(3,8),2)
									+", #"+Integer.parseInt(line.substring(16, 32), 2));
							insNameList.add(insName);
					}
				}
						
				pc += 4;
				}
			String simTrace = "";
			//simulation
			pc = basePC;
			int cycle = 1;
			while(!insNameList.get((pc-basePC)/4).contains("BREAK")){
				//Fetch Instruction
				if(waiting==-1 && executed == -1){
					String insNameString  = insNameList.get((pc-basePC)/4);
					if(insNameString.contains("J")||insNameString.contains("BEQ")||insNameString.contains("BGTZ")){
						if(insNameString.contains("J"))
							executed = (pc-basePC)/4;
						else if(insNameString.contains("BEQ") || insNameString.contains("BGTZ"))
							if(!jumpRegisterCheck((pc-basePC)/4,fetch1))
								waiting = (pc-basePC)/4;
							else
								executed = (pc-basePC)/4;
					}else{
						fetch1 = (pc-basePC)/4;
						pc+=4;
						insNameString  = insNameList.get((pc-basePC)/4);
						if(insNameString.contains("J")){
							executed = (pc-basePC)/4;
						}else if(insNameString.contains("BEQ") || insNameString.contains("BGTZ")){
							if(!jumpRegisterCheck((pc-basePC)/4,fetch1))
								waiting = (pc-basePC)/4;
							else
								executed = (pc-basePC)/4;
						}else{
							fetch2 = (pc-basePC)/4;
							pc+=4;
						}
					}
				}else if(waiting != -1){
					//check waiting 's register is ready: if not ready,stop fetch; if ready, move to executed and stop fetch
					if(jumpRegisterCheck(waiting,-1)){
						executed = waiting;
						waiting = -1;
					}
				}else if(executed != -1){
					//execute executed jump instruction and do not fetch instruction
					if(insNameList.get(executed).equals("J"))
						pc = ins.get(executed).get("address");
					else if(insNameList.get(executed).equals("BEQ")){
						int rs = ins.get(executed).get("rs");
						int rt = ins.get(executed).get("rt");
						if(gpr[rs] == gpr[rt])
							pc += ins.get(executed).get("offset")+4;
						else 
							pc +=4;
					}else if(insNameList.get(executed).equals("BGTZ")){
						int rs = ins.get(executed).get("rs");
						if(gpr[rs] > 0)
							pc += ins.get(executed).get("offset")+4;
						else 
							pc +=4;
					}
					executed = -1;
					String insNameString  = insNameList.get((pc-basePC)/4);
					if(insNameString.contains("J")||insNameString.contains("BEQ")||insNameString.contains("BGTZ")){
						if(insNameString.contains("J"))
							executed = (pc-basePC)/4;
						else if(insNameString.contains("BEQ") || insNameString.contains("BGTZ"))
							if(!jumpRegisterCheck((pc-basePC)/4,fetch1))
								waiting = (pc-basePC)/4;
							else
								executed = (pc-basePC)/4;
					}else if(insNameString.contains("BREAK")){
						executed = (pc-basePC)/4;
					}else{
						fetch1 = (pc-basePC)/4;
						pc+=4;
						insNameString  = insNameList.get((pc-basePC)/4);
						if(insNameString.contains("J")){
							executed = (pc-basePC)/4;
						}else if(insNameString.contains("BEQ") || insNameString.contains("BGTZ")){
							if(!jumpRegisterCheck((pc-basePC)/4,fetch1))
								waiting = (pc-basePC)/4;
							else
								executed = (pc-basePC)/4;
						}else{
							fetch2 = (pc-basePC)/4;
							pc+=4;
						}
					}
				}
				//Issue from preQueue
				int preALUSlot = 2 - preALU.size();	
				for(int i=0;i<preIssue.size();i++){
					//processing structural hazzard
					if(IssuedNum<2 &&preALUSlot != 0)
						if(!hazzardDetecter(preIssue.get(i),i))
							continue;
						else{
							IssuedNum++;
							preALUSlot--;
							tempIssueIntegers.add(preIssue.get(i));
						}
				}
				//update pre-Issue queue:delete issued Instruction ;add fetched issue
				for(Integer issuedID : tempIssueIntegers){
					for(int preIss=0;preIss<preIssue.size();preIss++)
						if(preIssue.get(preIss)==issuedID)
							preIssue.remove(preIss);
				}
				if(fetch1!=-1)
					preIssue.add(fetch1);
				if(fetch2!=-1)
					preIssue.add(fetch2);
				fetch1 = fetch2 = -1;
				//ALU pre processing
				if(preALU.size()>0){
					aluIns = preALU.get(0);
					if(ins.get(aluIns).get("category")==2 || ins.get(aluIns).get("category")==3)
						temppostALU  = aluIns;
					else if(insNameList.get(aluIns).contains("LW")||insNameList.get(aluIns).contains("SW"))
						tempPreMem = aluIns; 
				}

				//update pre_ALU queue
				if(preALU.size()>0){
					preALU.remove(0);
				}
				for(Integer issuedID : tempIssueIntegers)
					preALU.add(issuedID);
				tempIssueIntegers.clear();
				//Mem processing
				if(preMEM!=-1){
					if(insNameList.get(preMEM).contains("SW"))
						//update memory
						memories.put(gpr[ins.get(preMEM).get("base")]+ins.get(preMEM).get("offset"),gpr[ins.get(preMEM).get("rt")]);
					else
						tempPostMem = preMEM;
				}
				//LW post MEM processing
				if(postMEM!=-1)
					gpr[ins.get(postMEM).get("rt")] = memories.get(gpr[ins.get(postMEM).get("base")]+ins.get(postMEM).get("offset"));
				//ALU processing
				if(postALU!=-1){
					String insNameString = insNameList.get(postALU);
					if(insNameString.equals("ADD")){
						gpr[ins.get(postALU).get("rd")] = gpr[ins.get(postALU).get("rs")] + gpr[ins.get(postALU).get("rt")];
					}else if(insNameString.equals("SUB")){
						gpr[ins.get(postALU).get("rd")] = gpr[ins.get(postALU).get("rs")] - gpr[ins.get(postALU).get("rt")];
					}else if(insNameString.equals("MUL")){
						gpr[ins.get(postALU).get("rd")] = gpr[ins.get(postALU).get("rs")] * gpr[ins.get(postALU).get("rt")];
					}else if(insNameString.equals("OR")){
						gpr[ins.get(postALU).get("rd")] = gpr[ins.get(postALU).get("rs")] == 1 ? 1 : gpr[ins.get(postALU).get("rt")]== 1 ? 1 : 0;
					}else if(insNameString.equals("XOR")){
						gpr[ins.get(postALU).get("rd")] = gpr[ins.get(postALU).get("rs")] == gpr[ins.get(postALU).get("rt")] ? 0 : 1;
					}else if(insNameString.equals("NOR")){
						gpr[ins.get(postALU).get("rd")] = gpr[ins.get(postALU).get("rs")] == 1 ? 0 : gpr[ins.get(postALU).get("rt")] == 1 ? 0 : 1;
					}else if(insNameString.equals("ADDI")){
						gpr[ins.get(postALU).get("rt")] = gpr[ins.get(postALU).get("rs")] + ins.get(postALU).get("imme");
					}else if(insNameString.equals("ANDI")){
						gpr[ins.get(postALU).get("rt")] = gpr[ins.get(postALU).get("rs")] == ins.get(postALU).get("imme") ? 1 : 0;
					}else if(insNameString.equals("ORI")){
						gpr[ins.get(postALU).get("rt")] = gpr[ins.get(postALU).get("rs")] == 1 ? 1 : ins.get(postALU).get("imme") == 1 ? 1 : 0;
					}else if(insNameString.equals("XORI")){
						gpr[ins.get(postALU).get("rt")] = gpr[ins.get(postALU).get("rs")] == ins.get(postALU).get("imme") ? 0 : 1;
					}
				}
				//update all the queue

				//update pre_MEM
				if(tempPreMem!=-1)
					preMEM = tempPreMem;
				else
					preMEM = -1;
				if(temppostALU!=-1)
					postALU = temppostALU;
				else
					postALU = -1;
				if(tempPostMem!=-1)
					postMEM = tempPostMem;
				else
					postMEM = -1;
				fetch1 = fetch2 = aluIns = 
				temppostALU = tempPreMem = tempPostMem = -1;
				IssuedNum = 0;
				//print all messages
				simTrace += "--------------------\r"+"Cycle:"+ cycle++ +
						"\r\r"+"IF Unit:"+"\r\t"+"Waiting Instruction:"+ (waiting==-1 ? "":("["+insList.get(waiting)+"]"))+"\r"+
						"\t"+"Executed Instruction:"+ (executed==-1 ? "":"["+insList.get(executed)+"]")+"\r"+"Pre-Issue Queue:"+"\r";
				for(int i=0;i<preIssue.size();i++)
					simTrace+="\t"+"Entry "+i+":["+insList.get(preIssue.get(i))+"]\r";
				for(int i=preIssue.size();i<4;i++)
					simTrace+="\t"+"Entry "+i+":\r";
				simTrace += "Pre-ALU Queue:\r";
				for(int i=0;i<preALU.size();i++)
					simTrace+="\t"+"Entry "+i+":["+insList.get(preALU.get(i))+"]\r";
				for(int i=preALU.size();i<2;i++)
					simTrace+="\t"+"Entry "+i+":\r";
				simTrace += "Pre-MEM Queue:"+ (preMEM==-1 ? "":"["+insList.get(preMEM)+"]")+"\r";
				simTrace += "Post-MEM Queue:"+ (postMEM==-1 ? "":"["+insList.get(postMEM)+"]")+"\r";
				simTrace += "Post-ALU Queue:"+ (postALU==-1 ? "":"["+insList.get(postALU)+"]")+"\r";
				simTrace += "\rRegisters\r";	
					for(int i=0;i<4;i++){
						if(i*8<10)
							simTrace += "R0"+i*8 +":\t";
						else
							simTrace += "R"+i*8 +":\t";
						for(int j=0;j<8;j++)
							if(j==7)
								simTrace += gpr[i*8+j]+"\r";
							else 
								simTrace += gpr[i*8+j] + "\t";
					}
					simTrace += "\r" + "Data" + "\r";
					int memIndex = basePC + 4*ins.size();
					for(int i=0;i<2;i++){
						simTrace += memIndex +i*4*8 +":\t";
						memIndex += i*4*8;
						for(int j=0;j<8;j++){
							if(j!=7)
								simTrace += memories.get(memIndex + j*4)+ "\t";
							else
								simTrace += memories.get(memIndex + j*4);
						}
						simTrace += "\r";
					}
			}	
			simTrace = simTrace.substring(0, simTrace.length()-1);	
			writer = new OutputStreamWriter(new FileOutputStream("simulation.txt"));
			bw = new BufferedWriter(writer);
			bw.write(simTrace);
			bw.flush();
			bw.close();
				
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void main(String[] args){
		MIPSsim ms = new MIPSsim();
		ms.parse();

	}
	private void init(){
		category1.put("000", "J");
		category1.put("010", "BEQ");
		category1.put("100", "BGTZ");
		category1.put("101", "BREAK");
		category1.put("110", "SW");
		category1.put("111", "LW");
		
		category2.put("000", "ADD");
		category2.put("001", "SUB");
		category2.put("010", "MUL");
		category2.put("011", "AND");
		category2.put("100", "OR");
		category2.put("101", "XOR");
		category2.put("110", "NOR");
		
		category3.put("000", "ADDI");
		category3.put("001", "ANDI");
		category3.put("010", "ORI");
		category3.put("011", "XORI");
	}
	public String Encode(String str){
		char[] charList = str.toCharArray();
		String fuck = "";
		for(int i=0;i<charList.length;i++)
			fuck += Integer.parseInt(String.valueOf(charList[i]))==0? '1' : '0';
		return fuck;
	}
	public void Logic(){
		HashMap<String, Integer> current = ins.get((pc-basePC)/4);
		String insNameString = insNameList.get((pc-basePC)/4);
		if(insNameString.equals("J"))
			pc = current.get("address");
		else if(insNameString.equals("BEQ")){
			int rs = current.get("rs");
			int rt = current.get("rt");
			if(gpr[rs] == gpr[rt])
				pc += current.get("offset")+4;
			else 
				pc +=4;
		}else if(insNameString.equals("BGTZ")){
			int rs = current.get("rs");
			if(gpr[rs] > 0)
				pc += current.get("offset")+4;
			else 
				pc +=4;
		}else if(insNameString.equals("BREAK")){
			
		}else if(insNameString.equals("SW")){
			int rt = current.get("rt");
			int base = current.get("base");
			memories.put(gpr[base]+current.get("offset"),gpr[rt]);
			pc +=4;
		}else if(insNameString.equals("LW")){
			int rt = current.get("rt");
			int base = current.get("base");
			gpr[rt] = memories.get(gpr[base]+current.get("offset"));
			pc +=4;
		}else if(insNameString.equals("ADD")){
			gpr[current.get("rd")] = gpr[current.get("rs")] + gpr[current.get("rt")];
			pc +=4;
		}else if(insNameString.equals("SUB")){
			gpr[current.get("rd")] = gpr[current.get("rs")] - gpr[current.get("rt")];
			pc +=4;
		}else if(insNameString.equals("MUL")){
			gpr[current.get("rd")] = gpr[current.get("rs")] * gpr[current.get("rt")];
			pc +=4;
		}else if(insNameString.equals("OR")){
			gpr[current.get("rd")] = gpr[current.get("rs")] == 1 ? 1 : gpr[current.get("rt")]== 1 ? 1 : 0;
			pc +=4;
		}else if(insNameString.equals("XOR")){
			gpr[current.get("rd")] = gpr[current.get("rs")] == gpr[current.get("rt")] ? 0 : 1;
			pc +=4;
		}else if(insNameString.equals("NOR")){
			gpr[current.get("rd")] = gpr[current.get("rs")] == 1 ? 0 : gpr[current.get("rt")] == 1 ? 0 : 1;
			pc +=4;
		}else if(insNameString.equals("ADDI")){
			gpr[current.get("rt")] = gpr[current.get("rs")] + current.get("imme");
			pc +=4;
		}else if(insNameString.equals("ANDI")){
			gpr[current.get("rt")] = gpr[current.get("rs")] == current.get("imme") ? 1 : 0;
			pc +=4;
		}else if(insNameString.equals("ORI")){
			gpr[current.get("rt")] = gpr[current.get("rs")] == 1 ? 1 : current.get("imme") == 1 ? 1 : 0;
			pc +=4;
		}else if(insNameString.equals("XORI")){
			gpr[current.get("rt")] = gpr[current.get("rs")] == current.get("imme") ? 0 : 1;
			pc +=4;
		}
	}
	public boolean hazzardDetecter(int issueNum,int cFetchNum){
		String issueName  = insNameList.get(issueNum);
		if(issueName.contains("LW")|| issueName.contains("SW")){
			if(!LWSWRegisterCheck(issueNum,cFetchNum))
				return false;
		}else if(ins.get(issueNum).get("category")==2){
			//issued instruction WAW detection
			if(!C2RegisterCheck(issueNum,cFetchNum))
				return false;
		}else if(ins.get(issueNum).get("category")==3){
			if(!C3RegisterCheck(issueNum,cFetchNum))
				return false;
		}
		return true;
	}
	public boolean LWSWRegisterCheck(int issueNum,int cFetchNum){
		int rt = ins.get(issueNum).get("rt");
		int base = ins.get(issueNum).get("base");
		//preIssue register WAR WAW Hazzard detection
 		for(int i=0;i<cFetchNum;i++){
			if(ins.get(preIssue.get(i)).get("category")==1 && insNameList.get(preIssue.get(i)).contains("LW")){
				if(rt==ins.get(preIssue.get(i)).get("rt") || base==ins.get(preIssue.get(i)).get("rt"))
					return false;
			}
			if(ins.get(preIssue.get(i)).get("category")==2){
				int preIsRD = ins.get(preIssue.get(i)).get("rd");
				if(rt==preIsRD || base==preIsRD)
					return false;
			}
			if(ins.get(preIssue.get(i)).get("category")==3){
				int preIsRT = ins.get(preIssue.get(i)).get("rt");
				if(rt==preIsRT || base==preIsRT)
					return false;
			}
		}
		//preALU has register writen?
		for(Integer preA : preALU){
			if(ins.get(preA).get("category")==2)
				if(rt==ins.get(preA).get("rd") || base==ins.get(preA).get("rd"))
					return false;
			if(ins.get(preA).get("category")==3)
				if(rt==ins.get(preA).get("rt") || base==ins.get(preA).get("rt"))
					return false;
		}
		if(postALU!= -1){
			if(ins.get(postALU).get("category")==2)
				if(rt==ins.get(postALU).get("rd") || base==ins.get(postALU).get("rd"))
					return false;
			if(ins.get(postALU).get("category")==3)
				if(rt==ins.get(postALU).get("rt") || base==ins.get(postALU).get("rt"))
					return false;
		}
		return true;
	}
	public boolean C2RegisterCheck(int issueNum,int cFetchNum){
		int rt = ins.get(issueNum).get("rt");
		int rs = ins.get(issueNum).get("rs");
		int rd = ins.get(issueNum).get("rd");
		//preALU has register writen?
		for(Integer preA : preALU){
			if(ins.get(preA).get("category")==2){
				int preARD = ins.get(preA).get("rd");
				if(rt==preARD || rs==preARD || rd==preARD)
					return false;
			}
			if(ins.get(preA).get("category")==3){
				int preART = ins.get(preA).get("rt");
				if(rt==preART || rs==preART || rd==preART)
					return false;
			}else if(insNameList.get(preA).contains("LW")){
				int LWrt = ins.get(preA).get("rt");
				if(rt==LWrt || rs==LWrt || rd==LWrt)
					return false;
			}
		}
		//postALU has register writen????????
		if(postALU!= -1){
			if(ins.get(postALU).get("category")==2){
				int postARD = ins.get(postALU).get("rd");
				if(rt==postARD || rs==postARD || rd==postARD)
					return false;
			}
			if(ins.get(postALU).get("category")==3){
				int postART = ins.get(postALU).get("rt");
				if(rt==postART || rs==postART || rd==postART)
					return false;
			}
		}
		//preMEM hazzard
		if(preMEM!=-1){
			if(insNameList.get(preMEM).contains("LW")){
				int LWrt = ins.get(preMEM).get("rt");
				if(rt==LWrt || rs==LWrt || rd==LWrt)
					return false;
			}
		}
		//postMEM hazzard
		if(postMEM!=-1){
			if(insNameList.get(postMEM).contains("LW")){
				int LWrt = ins.get(postMEM).get("rt");
				if(rt==LWrt || rs==LWrt || rd==LWrt)
					return false;
			}
		}
		//preIssue register WAR WAW Hazzard detection
 		for(int i=0;i<cFetchNum;i++){
			if(ins.get(i).get("category")==1){
				if(rd==ins.get(preIssue.get(i)).get("base") || rd==ins.get(preIssue.get(i)).get("rt"))
					return false;
			}
			if(ins.get(preIssue.get(i)).get("category")==2){
				int preIsRT = ins.get(preIssue.get(i)).get("rt");
				int preIsRS = ins.get(preIssue.get(i)).get("rs");
				int preIsRD = ins.get(preIssue.get(i)).get("rd");
				if(rd==preIsRT || rd==preIsRS || rd==preIsRD)
					return false;
			}
			if(ins.get(i).get("category")==3){
				int preIsRT = ins.get(preIssue.get(i)).get("rt");
				int preIsRS = ins.get(preIssue.get(i)).get("rs");
				if(rd==preIsRT || rd==preIsRS)
					return false;
			}
		}
		return true;
	}
	public boolean C3RegisterCheck(int issueNum,int cFetchNum){
		int rt = ins.get(issueNum).get("rt");
		int rs = ins.get(issueNum).get("rs");
		//preALU has register writen?
		for(Integer preA : preALU){
			if(ins.get(preA).get("category")==2){
				int preARD = ins.get(preA).get("rd");
				if(rt==preARD || rs==preARD)
					return false;
			}
			if(ins.get(preA).get("category")==3){
				int preART = ins.get(preA).get("rt");
				if(rt==preART || rs==preART)
					return false;
			}
		}
		//postALU has register writen????????
		if(postALU!= -1){
			if(ins.get(postALU).get("category")==2){
				int postARD = ins.get(postALU).get("rd");
				if(rt==postARD || rs==postARD)
					return false;
			}
			if(ins.get(postALU).get("category")==3){
				int postART = ins.get(postALU).get("rt");
				if(rt==postART || rs==postART)
					return false;
			}
		}
		//preIssue register WAR WAW Hazzard detection
 		for(int i=0;i<cFetchNum;i++){
			if(ins.get(preIssue.get(i)).get("category")==1){
				if(rt==ins.get(preIssue.get(i)).get("base") || rt==ins.get(preIssue.get(i)).get("rt"))
					return false;
			}
			if(ins.get(preIssue.get(i)).get("category")==2){
				int preIsRT = ins.get(preIssue.get(i)).get("rt");
				int preIsRS = ins.get(preIssue.get(i)).get("rs");
				int preIsRD = ins.get(preIssue.get(i)).get("rd");
				if(rt==preIsRT || rt==preIsRS || rt==preIsRD)
					return false;
			}
			if(ins.get(preIssue.get(i)).get("category")==3){
				int preIsRT = ins.get(preIssue.get(i)).get("rt");
				int preIsRS = ins.get(preIssue.get(i)).get("rs");
				if(rt==preIsRT || rt==preIsRS)
					return false;
			}
		}
		return true;
	}
	public boolean jumpRegisterCheck(int issueNum,int fetch1){
		List<Integer> rList = new ArrayList<Integer>();
		String insNameString  = insNameList.get(issueNum);
		if(insNameString.contains("BEQ")){
			rList.add(ins.get(issueNum).get("rs"));
			rList.add(ins.get(issueNum).get("rt"));
		}else if(insNameString.contains("BGTZ")){
			rList.add(ins.get(issueNum).get("rs"));
		}
		//preALU preIssue has register writen?
		for(Integer reg : rList){
			if(fetch1!=-1){
				if(ins.get(fetch1).get("category")==2){
					int preARD = ins.get(fetch1).get("rd");
					if(reg==preARD)
						return false;
				}
				if(ins.get(fetch1).get("category")==3){
					int preART = ins.get(fetch1).get("rt");
					if(reg==preART)
						return false;
				}
			}
			for(Integer preA : preIssue){
				if(ins.get(preA).get("category")==2){
					int preARD = ins.get(preA).get("rd");
					if(reg==preARD)
						return false;
				}
				if(ins.get(preA).get("category")==3){
					int preART = ins.get(preA).get("rt");
					if(reg==preART)
						return false;
				}
			}
			for(Integer preA : preALU){
				if(ins.get(preA).get("category")==2){
					int preARD = ins.get(preA).get("rd");
					if(reg==preARD)
						return false;
				}
				if(ins.get(preA).get("category")==3){
					int preART = ins.get(preA).get("rt");
					if(reg==preART)
						return false;
				}
			}
			//postALU has register writen????????
			if(postALU!= -1){
				if(ins.get(postALU).get("category")==2){
					int postARD = ins.get(postALU).get("rd");
					if(reg==postARD)
						return false;
				}
				if(ins.get(postALU).get("category")==3){
					int postART = ins.get(postALU).get("rt");
					if(reg==postART)
						return false;
				}
			}
		}
		return true;
	}
	
}
