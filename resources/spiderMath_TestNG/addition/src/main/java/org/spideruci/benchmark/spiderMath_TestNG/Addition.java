package org.spideruci.benchmark.spiderMath_TestNG;

public class Addition {

	public static int add(int a, int b){
		if(a>5) System.out.println("Bigger than 5: "+a);
		return a+b;
	}
	
	public static double add(Double a, Double b){
		return  a+b;
	}
}
