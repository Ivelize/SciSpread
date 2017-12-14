package builder;

import cerg.ddex.openspreadsheet.builder.ISpreadsheetBuilder;
import cerg.ddex.util.image.ImageProperties;

public abstract class AbstractBuilder implements ISpreadsheetBuilder {

	@Override
	public void foundBlankContentInCell(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void foundBlankLine(int arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void foundCell(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void foundCellContentAsBoolean(int arg0, int arg1, int arg2,
			boolean arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void foundCellContentAsDouble(int arg0, int arg1, int arg2,
			double arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void foundCellContentAsFormula(int arg0, int arg1, int arg2,
			String arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void foundCellContentAsString(int arg0, int arg1, int arg2,
			String arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void foundCellDesignProperties(int arg0, int arg1, int arg2,
			String arg3, String arg4, String arg5, String arg6, boolean arg7,
			boolean arg8, boolean arg9, boolean arg10, int arg11, int arg12,
			int arg13, int arg14, int arg15, String arg16) {
		// TODO Auto-generated method stub

	}

	@Override
	public void foundCellEnd(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void foundCellTextProperties(int arg0, int arg1, int arg2, int arg3,
			String arg4, String arg5, boolean arg6, boolean arg7, boolean arg8) {
		// TODO Auto-generated method stub

	}

	@Override
	public void foundDeletedLabel(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void foundEndOfSS() {
		// TODO Auto-generated method stub

	}

	@Override
	public void foundFilePath(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void foundImage(int arg0, String arg1, String arg2, byte[] arg3,
			ImageProperties arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void foundLabeledCell(String arg0, int arg1, String arg2, int arg3,
			int arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void foundLabeledCellAsDouble(int arg0, int arg1, int arg2,
			double arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void foundLabeledCellAsFormula(int arg0, int arg1, int arg2,
			String arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void foundLabeledCellAsString(int arg0, int arg1, int arg2,
			String arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void foundLabeledCellEnd(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void foundLabeledEmptyCell(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void foundLastLabeledCellEnd() {
		// TODO Auto-generated method stub

	}

	@Override
	public void foundLine(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void foundLineEnd(int arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void foundNotUsedCell(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void foundNotUsedLabeledCell(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void foundNumberOfLabeledCells(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void foundPage(int arg0, String arg1, boolean arg2, int arg3,
			int arg4, int arg5) {
		// TODO Auto-generated method stub

	}

	@Override
	public void foundPageEnd(int arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void foundPagesQuantity(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void foundQuantityOfImages(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void foundSSBegin() {
		// TODO Auto-generated method stub

	}

	@Override
	public void foundUnknowLabeledCell(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void foundUnknownCellContent(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
