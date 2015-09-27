package com.docactivator;

import java.util.Set;
import java.util.TreeMap;

public class Captions {
	private static TreeMap<String, Integer> tmC2ID = new TreeMap<String, Integer>();
	private static TreeMap<String, String> tmC2RC = new TreeMap<String, String>();
	static {
		tmC2ID.put("ხარჯი მ3", 114);
		tmC2ID.put("ძველი ჩვენება", 113);
		tmC2ID.put("ძველი მრიცხველი", 112);
		tmC2ID.put("ცვლილება", 111);
		tmC2ID.put("ჩვენება", 110);
		tmC2ID.put("შენიშვნა", 109);
		tmC2ID.put("ქუჩა", 108);
		tmC2ID.put("ქარხნული N", 107);
		tmC2ID.put("ქალაქი", 106);
		tmC2ID.put("სტატუსი", 105);
		tmC2ID.put("სერია/სახელი", 104);
		tmC2ID.put("რეგიონი", 103);
		tmC2ID.put("რაიონი", 102);
		tmC2ID.put("მრიცხველის ჩვენება", 101);
		tmC2ID.put("მრიცხველის ტიპი", 100);
		tmC2ID.put("მრიცხველი", 99);
		tmC2ID.put("მრიცხველები", 98);
		tmC2ID.put("მონტაჟის თარიღი", 97);
		tmC2ID.put("მონტაჟის ადგილი", 96);
		tmC2ID.put("მისამართი", 95);
		tmC2ID.put("მაკორექ. თანხა", 94);
		tmC2ID.put("მაკორექ. თანხა", 93);
		tmC2ID.put("ლუქი", 92);
		tmC2ID.put("კრედიტი", 91);
		tmC2ID.put("კორექტორის ჩვენება", 90);
		tmC2ID.put("კორექტორის ტიპი", 89);
		tmC2ID.put("კორექტორის N", 88);
		tmC2ID.put("კორექტირებადი დოკუმენტი(#:აღწ:დრო ---თანხა:კანც#)", 87);
		tmC2ID.put("ინდ.დარიცხვა", 86);
		tmC2ID.put("თარიღი", 85);
		tmC2ID.put("ზონა", 84);
		tmC2ID.put("გადახდის თანხა", 82);
		tmC2ID.put("ბანკი", 81);
		tmC2ID.put("ახალი ჩვენება", 80);
		tmC2ID.put("ახალი სტატუსი", 79);
		tmC2ID.put("ახალი მრიცხველი ძირითადი", 78);
		tmC2ID.put("ახალი მრიცხველი კორექტორი", 77);
		tmC2ID.put("ახალი ზონა", 76);
		tmC2ID.put("აბონენტის სახელი", 75);
		tmC2ID.put("ზონა", 74);
		tmC2ID.put("სამუშაო ადგილი", 73);
		tmC2ID.put("ტარიფი", 72);
		tmC2ID.put("რაიონი", 71);
		tmC2ID.put("ქუჩა", 70);
		tmC2ID.put("რეგიონი", 69);
		tmC2ID.put("პირადი ნომერი", 68);
		tmC2ID.put("ტელეფონი", 67);
		tmC2ID.put("პირადი ნომერი", 66);
		tmC2ID.put("პირადი ინფორმაცია", 65);
		tmC2ID.put("სხვა", 64);
		tmC2ID.put("საიდენტიფიკაციო კოდი", 63);
		tmC2ID.put("მიწოდებული გაზი", 62);
		tmC2ID.put("ბინა", 61);
		tmC2ID.put("დოკუმენტის #", 60);
		tmC2ID.put("თარიღი", 59);
		tmC2ID.put("აბონენტის სახელი", 58);
		tmC2ID.put("აბონენტის მდგომარეობა", 57);
		tmC2ID.put("აბონენტის საქმიანობა", 56);
		tmC2ID.put("აბონენტის ტიპი", 55);
		tmC2ID.put("აბონენტის კლასი", 54);
		tmC2ID.put("ქალაქი", 53);
		tmC2ID.put("საქმიანობის სფერო", 52);
		tmC2ID.put("საქმიანობა", 51);
		tmC2ID.put("შენობის #", 50);
		tmC2ID.put("შენობის ტიპი", 49);
		tmC2ID.put("საბიუჯეტო/არასაბიუჯეტო", 48);
		tmC2ID.put("ბანკის კოდი", 47);
		tmC2ID.put("მისამართი", 46);
		tmC2ID.put("ვალი", 45);
		tmC2ID.put("აბონენტის #", 44);

		tmC2RC.put("Address", "მისამართი");
		tmC2RC.put("Bank Iden. Code", "ბანკის კოდი");
		tmC2RC.put("Budjet/Non Budjet", "საბიუჯეტო/არასაბიუჯეტო");
		tmC2RC.put("Build.type", "შენობის ტიპი");
		tmC2RC.put("Building Num", "შენობის #");
		tmC2RC.put("Buisness", "საქმიანობა");
		tmC2RC.put("Buisness area", "საქმიანობის სფერო");
		tmC2RC.put("City", "ქალაქი");
		tmC2RC.put("Cust Class", "აბონენტის კლასი");
		tmC2RC.put("Cust Type", "აბონენტის ტიპი");
		tmC2RC.put("Cust. Buisness", "აბონენტის საქმიანობა");
		tmC2RC.put("Cust. State", "აბონენტის მდგომარეობა");
		tmC2RC.put("Customer Name", "აბონენტის სახელი");
		tmC2RC.put("Date", "თარიღი");
		tmC2RC.put("DocNo", "დოკუმენტის #");
		tmC2RC.put("Flat", "ბინა");
		tmC2RC.put("Given Pressure", "მიწოდებული გაზი");
		tmC2RC.put("Identity code", "საიდენტიფიკაციო კოდი");
		tmC2RC.put("Other", "სხვა");
		tmC2RC.put("Personal Info", "პირადი ინფორმაცია");
		tmC2RC.put("Personal Num", "პირადი ნომერი");
		tmC2RC.put("Phone", "ტელეფონი");
		tmC2RC.put("Private number", "პირადი ნომერი");
		tmC2RC.put("Region", "რეგიონი");
		tmC2RC.put("Street", "ქუჩა");
		tmC2RC.put("Sub Region", "რაიონი");
		tmC2RC.put("Tarif Plan", "ტარიფი");
		tmC2RC.put("Working Place", "სამუშაო ადგილი");
		tmC2RC.put("Zone", "ზონა");
		tmC2RC.put("აბონენტის #", "აბონენტის #");
		tmC2RC.put("აბონენტის სახელი", "აბონენტის სახელი");
		tmC2RC.put("ახალი ზონა", "ახალი ზონა");
		tmC2RC.put("ახალი მრიცხველი კორექტორი", "ახალი მრიცხველი კორექტორი");
		tmC2RC.put("ახალი მრიცხველი ძირითადი", "ახალი მრიცხველი ძირითადი");
		tmC2RC.put("ახალი სტატუსი", "ახალი სტატუსი");
		tmC2RC.put("ახალი ჩვენება", "ახალი ჩვენება");
		tmC2RC.put("ბანკი", "ბანკი");
		tmC2RC.put("გადახდის თანხა", "გადახდის თანხა");
		tmC2RC.put("ვალი", "ვალი");
		tmC2RC.put("ზონა", "ზონა");
		tmC2RC.put("თარიღი", "თარიღი");
		tmC2RC.put("ინდ.დარიცხვა", "ინდ.დარიცხვა");
		tmC2RC.put("კორექტირებადი დოკუმენტი(#:აღწ:დრო ---თანხა:კანც#)",
				"კორექტირებადი დოკუმენტი(#:აღწ:დრო ---თანხა:კანც#)");
		tmC2RC.put("კორექტორის N", "კორექტორის N");
		tmC2RC.put("კორექტორის ტიპი", "კორექტორის ტიპი");
		tmC2RC.put("კორექტორის ჩვენება", "კორექტორის ჩვენება");
		tmC2RC.put("კრედიტი", "კრედიტი");
		tmC2RC.put("ლუქი", "ლუქი");
		tmC2RC.put("მაკონტ. თანხა", "მაკორექ. თანხა");
		tmC2RC.put("მაკორექ. თანხა", "მაკორექ. თანხა");
		tmC2RC.put("მისამართი", "მისამართი");
		tmC2RC.put("მონტაჟის ადგილი", "მონტაჟის ადგილი");
		tmC2RC.put("მონტაჟის თარიღი", "მონტაჟის თარიღი");
		tmC2RC.put("მრიცხველები", "მრიცხველები");
		tmC2RC.put("მრიცხველი", "მრიცხველი");
		tmC2RC.put("მრიცხველის ტიპი", "მრიცხველის ტიპი");
		tmC2RC.put("მრიცხველის ჩვენება", "მრიცხველის ჩვენება");
		tmC2RC.put("რაიონი", "რაიონი");
		tmC2RC.put("რეგიონი", "რეგიონი");
		tmC2RC.put("სერია/სახელი", "სერია/სახელი");
		tmC2RC.put("სტატუსი", "სტატუსი");
		tmC2RC.put("ქალაქი", "ქალაქი");
		tmC2RC.put("ქარხნული N", "ქარხნული N");
		tmC2RC.put("ქუჩა", "ქუჩა");
		tmC2RC.put("შენიშვნა", "შენიშვნა");
		tmC2RC.put("ჩვენება", "ჩვენება");
		tmC2RC.put("ცვლილება", "ცვლილება");
		tmC2RC.put("ძველი მრიცხველი", "ძველი მრიცხველი");
		tmC2RC.put("ძველი ჩვენება", "ძველი ჩვენება");
		tmC2RC.put("ხარჯი მ3", "ხარჯი მ3");
	}

	public static void main(String[] args) {
		
		
		
		String insertCommand = "insert into captions values(%d,%d,'%s');";
		Set<String> keys = tmC2RC.keySet();
		TreeMap<Integer, String> mapIdOldString = new TreeMap<Integer, String>();
		for (String key : keys) {
			mapIdOldString.put(getCaptionID(key), key);
		}
		keys = tmC2ID.keySet();

		for (String key : keys) {
			System.out.println(String.format(insertCommand, tmC2ID.get(key), 1,
					key));
			System.out.println(String.format(insertCommand, tmC2ID.get(key), 3,
					key));
			String value=mapIdOldString.get(tmC2ID.get(key));
			if(value==null)
				value=key;	
			System.out.println(String.format(insertCommand, tmC2ID.get(key), 2,
					value));
		}
		
		for (String key : keys) {
			String k = tmC2ID.get(key).toString() + "%";
			
			System.out.println(k + key.trim()+"%"+mapIdOldString.get(tmC2ID.get(key)));
		}

	}

	public static int getCaptionID(String caption) {
		caption = caption.trim();
		caption = tmC2RC.get(caption);
		if (caption != null) {
			Integer ret = tmC2ID.get(caption);
			if (ret != null)
				return ret.intValue();
		}

		return -1;
	}
}
