package org.spideruci.tacoco.PIT;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Mutation {
	
	int m_id;
	boolean detected;//<mutation detected='true' status='KILLED'>
	String status;//<mutation detected='true' status='KILLED'>
	String FQN_target_class; //<mutatedClass>mvntest.KMath</mutatedClass>
	String FQN_target_method; //<mutatedMethod>add</mutatedMethod>
	int lineNum; //<lineNumber>6</lineNumber>
	String mutator; //<mutator>org.pitest.mutationtest.engine.gregor.mutators.ConditionalsBoundaryMutator</mutator>
	int index;//<index>5</index>
	String[] killingTests;//<killingTest/></mutation>
	int src_id;

	public Mutation(int id, Node node){
		this.m_id = id;
		this.detected = Boolean.parseBoolean(node.getAttributes().getNamedItem("detected").getNodeValue());
		this.status = node.getAttributes().getNamedItem("status").getNodeValue();
		NodeList children = node.getChildNodes();
		for(int i=0; i<children.getLength(); ++i){
			String name = children.item(i).getNodeName();
			String val = children.item(i).getTextContent();
			//System.out.println(name+":"+val);
			switch(name){
				case "mutatedClass":
					this.FQN_target_class = val; break;
				case "mutatedMethod":
					this.FQN_target_method = val; break;
				case "lineNumber":
					this.lineNum = Integer.parseInt(val); break;
				case "mutator":
					this.mutator = val; break;
				case "index":
					this.index = Integer.parseInt(val); break;
				case "killingTest":
					this.killingTests = val.split(","); 
					//remove package name prefix to compare this to jacoco output
					for(int j=0; j<killingTests.length; ++j){
						String s = killingTests[j];
						killingTests[j] = s.replaceFirst(s.replaceFirst("(.*)\\((.*)\\)$", "$2")+".", ""); 
					}
					break;
			}
		}
	}
	
	public String toString(){
		return "detected: "+detected+" status: "+status;
	}

}
