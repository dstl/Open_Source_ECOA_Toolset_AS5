/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.common;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorPart;

@SuppressWarnings("deprecation")
public class ImageSaveUtil {
	public static boolean save(IEditorPart editorPart, GraphicalViewer viewer, String saveFilePath, int format) {
		Assert.isNotNull(editorPart, "null editorPart passed to ImageSaveUtil::save");
		Assert.isNotNull(viewer, "null viewer passed to ImageSaveUtil::save");
		Assert.isNotNull(saveFilePath, "null saveFilePath passed to ImageSaveUtil::save");

		if (format != SWT.IMAGE_BMP && format != SWT.IMAGE_JPEG && format != SWT.IMAGE_ICO)
			throw new IllegalArgumentException("Save format not supported");
		try {
			saveEditorContentsAsImage(editorPart, viewer, saveFilePath, format);
		} catch (Exception ex) {
			MessageDialog.openError(editorPart.getEditorSite().getShell(), "Save Error", "Could not save editor contents");
			return false;
		}
		return true;
	}

	public static boolean save(IEditorPart editorPart, GraphicalViewer viewer) {
		Assert.isNotNull(editorPart, "null editorPart passed to ImageSaveUtil::save");
		Assert.isNotNull(viewer, "null viewer passed to ImageSaveUtil::save");

		String saveFilePath = getSaveFilePath(editorPart, viewer, -1);
		if (saveFilePath == null)
			return false;
		int format = SWT.IMAGE_JPEG;
		if (saveFilePath.endsWith(".jpeg"))
			format = SWT.IMAGE_JPEG;
		else if (saveFilePath.endsWith(".bmp"))
			format = SWT.IMAGE_BMP;
		// else if (saveFilePath.endsWith(".ico"))
		// format = SWT.IMAGE_ICO;
		return save(editorPart, viewer, saveFilePath, format);
	}

	private static String getSaveFilePath(IEditorPart editorPart, GraphicalViewer viewer, int format) {
		FileDialog fileDialog = new FileDialog(editorPart.getEditorSite().getShell(), SWT.SAVE);
		String[] filterExtensions = new String[] { "*.jpeg",
				"*.bmp"/*
						 * , "*.ico" , "*.png", "*.gif"
						 */ };
		if (format == SWT.IMAGE_BMP)
			filterExtensions = new String[] { "*.bmp" };
		else if (format == SWT.IMAGE_JPEG)
			filterExtensions = new String[] { "*.jpeg" };
		// else if (format == SWT.IMAGE_ICO)
		// filterExtensions = new String[] { "*.ico" };
		fileDialog.setFilterExtensions(filterExtensions);

		return fileDialog.open();
	}

	private static void saveEditorContentsAsImage(IEditorPart editorPart, GraphicalViewer viewer, String saveFilePath, int format) {

		ScalableFreeformRootEditPart rootEditPart = (ScalableFreeformRootEditPart) viewer.getEditPartRegistry().get(LayerManager.ID);
		IFigure rootFigure = ((LayerManager) rootEditPart).getLayer(LayerConstants.PRINTABLE_LAYERS);// rootEditPart.getFigure();
		Rectangle rootFigureBounds = rootFigure.getBounds();

		Control figureCanvas = viewer.getControl();
		GC figureCanvasGC = new GC(figureCanvas);

		Image img = new Image(null, rootFigureBounds.width, rootFigureBounds.height);
		GC imageGC = new GC(img);
		imageGC.setBackground(figureCanvasGC.getBackground());
		imageGC.setForeground(figureCanvasGC.getForeground());
		imageGC.setFont(figureCanvasGC.getFont());
		imageGC.setLineStyle(figureCanvasGC.getLineStyle());
		imageGC.setLineWidth(figureCanvasGC.getLineWidth());
		imageGC.setXORMode(figureCanvasGC.getXORMode());
		Graphics imgGraphics = new SWTGraphics(imageGC);

		rootFigure.paint(imgGraphics);

		ImageData[] imgData = new ImageData[1];
		imgData[0] = img.getImageData();

		ImageLoader imgLoader = new ImageLoader();
		imgLoader.data = imgData;
		imgLoader.save(saveFilePath, format);

		figureCanvasGC.dispose();
		imageGC.dispose();
		img.dispose();
	}
}