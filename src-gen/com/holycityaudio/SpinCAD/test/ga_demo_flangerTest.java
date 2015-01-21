/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * ga_demo_flangerTest.java
 * Copyright (C) 2015 - Gary Worsham 
 * Based on ElmGen by Andrew Kilpatrick 
 * 
 *   This program is free software: you can redistribute it and/or modify 
 *   it under the terms of the GNU General Public License as published by 
 *   the Free Software Foundation, either version 3 of the License, or 
 *   (at your option) any later version. 
 * 
 *   This program is distributed in the hope that it will be useful, 
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 *   GNU General Public License for more details. 
 * 
 *   You should have received a copy of the GNU General Public License 
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 *     
 */ 
 
package com.holycityaudio.SpinCAD.test;

import com.holycityaudio.SpinCAD.SpinCADFrame;
import com.holycityaudio.SpinCAD.SpinCADPanel;
import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.CADBlocks.*;

public class ga_demo_flangerTest {
	public ga_demo_flangerTest(SpinCADFrame f) {
		System.out.println("Start of ga_demo_flangerCADBlock unit test..." + "\n");

		SpinCADPanel p = new SpinCADPanel(f);
		InputCADBlock i = new InputCADBlock(225, 10);
		OutputCADBlock o = new OutputCADBlock(225, 290);
		ga_demo_flangerCADBlock m = new ga_demo_flangerCADBlock(225, 210);
f.dropBlock(p, m);	

f.getModel().sortAlignGen();
System.out.println("ga_demo_flangerCADBlock test passed with no connections!");
f.getModel().newModel();
// now do "all control connections"
f.dropBlock(p, i);	
f.dropBlock(p, m);	
f.dropBlock(p, o);	
{
	SpinCADPin p1 = i.getPin("Output 1");
	SpinCADPin p2 = m.getPin("Input_Left");
	p2.setConnection(i, p1);
}
{
	SpinCADPin p1 = i.getPin("Output 2");
	SpinCADPin p2 = m.getPin("Input_Right");
	p2.setConnection(i, p1);
}
{
	SpinCADPin p1 = o.getPin("Input 1");
	SpinCADPin p2 = m.getPin("Audio_Output_1");
	p2.setConnection(o, p1);
}
{
	SpinCADPin p1 = o.getPin("Input 2");
	SpinCADPin p2 = m.getPin("Audio_Output_2");
	p2.setConnection(o, p1);
}
{
	Pot0CADBlock pot0 = new Pot0CADBlock(25, 150 + 40 * 1);
	f.dropBlock(p, pot0);
	SpinCADPin p1 = pot0.getPin("Output 1");
	SpinCADPin p2 = m.getPin("Reverb_Level");
	p2.setConnection(pot0, p1);
}
{
	Pot1CADBlock pot1 = new Pot1CADBlock(25, 150 + 40 * 2);
	f.dropBlock(p, pot1);
	SpinCADPin p1 = pot1.getPin("Output 1");
	SpinCADPin p2 = m.getPin("Flange_Rate");
	p2.setConnection(pot1, p1);
}
{
	Pot2CADBlock pot2 = new Pot2CADBlock(25, 150 + 40 * 3);
	f.dropBlock(p, pot2);
	SpinCADPin p1 = pot2.getPin("Output 1");
	SpinCADPin p2 = m.getPin("Effect_Level_Feedback");
	p2.setConnection(pot2, p1);
}
f.getModel().sortAlignGen();
System.out.println("ga_demo_flangerCADBlock test passed with all control connections!");

f.getModel().newModel();	

// end of test code
	System.out.println("End of ga_demo_flangerCADBlock unit test" + "\n");
	}
}
