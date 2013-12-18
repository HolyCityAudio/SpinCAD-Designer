/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * SpinCADModel.java
 * Copyright (C)2013 - Gary Worsham
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

package com.holycityaudio.SpinCAD;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import org.andrewkilpatrick.elmGen.ElmProgram;

public class SpinCADModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8461943977905967897L;
	String name = "SpinCADModel";
	ArrayList<SpinCADBlock> blockList = null;
	private static SpinCADBlock currentBlock = null;
	public static SpinFXBlock renderBlock = null;
	boolean changed = false;
	static int nBlocks = 0;

	public SpinCADModel() {
		// TODO Auto-generated constructor stub
		// super();
		newModel();
	}

	// newModel() clears out the current list
	public void newModel() {
		nBlocks = 0;
		blockList = new ArrayList<SpinCADBlock>();
		changed = false;
		setRenderBlock(new SpinFXBlock("Render Block"));
}

	public int addBlock(SpinCADBlock pCB) {
		nBlocks++;
		blockList.add(pCB);
		pCB.setBlockNum(nBlocks);
		setCurrentBlock(pCB);
		changed = true;
		return 0;
	}

	public int modelSort() {
		Iterator<SpinCADBlock> itr = blockList.iterator();
//		System.out.printf("Model sort...\n", name);
		SpinCADBlock block;
		int b = -1;
		int nSwaps = 0;

		while (itr.hasNext()) {
			block = itr.next();
//			System.out.printf("Block %s [%d]\n", block.getName(), b);
			Iterator<SpinCADPin> itrPin = block.pinList.iterator();
			SpinCADPin currentPin;

			while (itrPin.hasNext()) {
				currentPin = itrPin.next();
				SpinCADBlock cB = currentPin.getBlockConnection();
				SpinCADPin cP = currentPin.getPinConnection();
				if (cB != null && cP != null) {
					// System.out.printf("Original [%s][%d] pin:[%s] ==> [%s] [%d] pin:[%s]\n",
					// block.getName(), block.getBlockNum(),
					// currentPin.getName(), cB.getName(), cB.getBlockNum(),
					// cP.getName());
					b = block.getBlockNum();
					if (b < cB.getBlockNum()) {
						int c = cB.getBlockNum();
						block.setBlockNum(c);
						cB.setBlockNum(b);
						nSwaps++;
/*						System.out.printf("Swapped [%s][%d] pin:[%s] ==> [%s] [%d] pin:[%s]\n\n",
										block.getName(), block.getBlockNum(),
										currentPin.getName(), cB.getName(),
										cB.getBlockNum(), cP.getName());
*/					}
				}
			}
		}
		return nSwaps;
	}

	public int setConnection(int sink, String sinkPinName, int source,
			String sourcePinName) {
		Iterator<SpinCADBlock> itr = blockList.iterator();
		System.out.printf("Set connection...\n", name);
		SpinCADBlock block;
		SpinCADBlock sourceBlock = null;
		SpinCADPin sourcePin = null;
		int b = -1;

		while (itr.hasNext()) {
			block = itr.next();
			b = block.getBlockNum();
			if (b == sink) {
//				System.out.printf("Sink %s [%d]\n", block.getName(), b);
				Iterator<SpinCADPin> itrPin = block.pinList.iterator();
				SpinCADPin currentPin;

				while (itrPin.hasNext()) {
					currentPin = itrPin.next();
					if (currentPin.getName() == sinkPinName) {
//						System.out.printf("Sink pin %s\n", sinkPinName);
						currentPin.setConnection(sourceBlock, sourcePin);
					}
				}
			}
			if (b == source) {
//				System.out.printf("Source %s [%d]\n", block.getName(), b);
				Iterator<SpinCADPin> itrPin = block.pinList.iterator();
				SpinCADPin currentPin;

				while (itrPin.hasNext()) {
					currentPin = itrPin.next();
					if (currentPin.getName() == sourcePinName) {
						sourcePin = currentPin;
//						System.out.printf("Source pin %s\n", sinkPinName);
					}
				}
			}
		}
		return 0;
	}

	public SpinCADBlock getBlock(int blockNum) {
		Iterator<SpinCADBlock> itr = blockList.iterator();
		// System.out.printf("get Block...\n", name);
		SpinCADBlock block;
		int b = -1;

		while (itr.hasNext()) {
			block = itr.next();
			b = block.getBlockNum();
			if (b == blockNum) {
				// System.out.printf("Name %s [%d]\n", block.getName(), b);
				return block;
			}
		}
		return null;
	}

	public int realign() {
		ArrayList<SpinCADBlock> sortedList = new ArrayList<SpinCADBlock>();

//		System.out.printf("Realign...\n", name);
		SpinCADBlock block;
		int blockNumMin = 32768;
		SpinCADBlock blockMin = null;

		Iterator<SpinCADBlock> itr = blockList.iterator();
		while (itr.hasNext()) {

			Iterator<SpinCADBlock> itr2 = blockList.iterator();
			while (itr2.hasNext()) {
				block = itr2.next();
				if (block.getBlockNum() < blockNumMin) {
					blockMin = block;
					blockNumMin = block.getBlockNum();
				}
				// now blockMin is the block with the lowest number
			}
			sortedList.add(blockMin);
			// System.out.println(sortedList);
			blockList.remove(blockMin);
			// System.out.println(blockList);

			// adjust iterator and blockNumMin after removing lowest numbered
			// block
			itr = blockList.iterator();
			blockNumMin = 32768;
		}

		blockList = sortedList;
		return 0;
	}

	public int forEachBlock() {
		@SuppressWarnings("unused")
		SpinCADBlock block = null;
		Iterator<SpinCADBlock> itr = blockList.iterator();
		while (itr.hasNext()) {
			block = itr.next();

			// now blockMin is the block with the lowest number
		}
		return 0;
	}
	
	public int sortAlignGen() {
		@SuppressWarnings("unused")
		SpinCADBlock block = null;
		Iterator<SpinCADBlock> itr = blockList.iterator();
		while (itr.hasNext()) {
			block = itr.next();
			if(modelSort() == 0)
				break;
		}		
		realign();
		int i = generateCode();
		ElmProgram.checkCodeLen();
		return i;
	}

	public int generateCode() {
		setRenderBlock(new SpinFXBlock("Render Block"));
		SpinCADBlock block = null;
		Iterator<SpinCADBlock> itr = blockList.iterator();
		while (itr.hasNext()) {
			block = itr.next();
			getRenderBlock();
			SpinFXBlock.setNumBlocks(block.getBlockNum());	// this is for keeping delay segments unique
			block.generateCode(getRenderBlock());
			// now blockMin is the block with the lowest number
		}
		System.out.println("Program Listing");
		System.out.println(getRenderBlock().getProgramListing(1));
		return getRenderBlock().getCodeLen() - getRenderBlock().getNumComments();
	}

	public static SpinFXBlock getRenderBlock() {
		return renderBlock;
	}

	public static void setRenderBlock(SpinFXBlock renderBlock) {
		SpinCADModel.renderBlock = renderBlock;
	}

	public static SpinCADBlock getCurrentBlock() {
		return currentBlock;
	}

	public static void setCurrentBlock(SpinCADBlock currentBlock) {
		SpinCADModel.currentBlock = currentBlock;
	}

	public void setChanged(boolean b) {
		// TODO Auto-generated method stub
		changed = b;
	}
	public boolean getChanged() {
		// TODO Auto-generated method stub
		return changed;
	}
}
