package com.merck.uploader.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.merck.uploader.modal.ResumableInfo;
import com.merck.uploader.modal.ResumableInfoStorage;
import com.merck.uploader.util.HttpUtils;


@CrossOrigin("*")
@RestController
@RequestMapping(value = { "/v1/app" })
public class AppController {
	
	@Value("${upload-file-path}")
	public String UPLOAD_DIR;
	
	@RequestMapping(value="/upload", method = RequestMethod.GET)
	public void getUpload(HttpServletResponse response, HttpServletRequest request) throws ServletException, IOException {
		
		int resumableChunkNumber        = getResumableChunkNumber(request);
		ResumableInfo info = getResumableInfo(request);

        if (info.uploadedChunks.contains(new ResumableInfo.ResumableChunkNumber(resumableChunkNumber))) {
            response.getWriter().print("Uploaded."); //This Chunk has been Uploaded.
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
	}
	
	
	@RequestMapping(value="/upload", method = RequestMethod.POST)
	public void upload(HttpServletResponse response, HttpServletRequest request) throws ServletException  {
		
		int resumableChunkNumber = getResumableChunkNumber(request);
		
		 ResumableInfo info = getResumableInfo(request);
		 RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(info.resumableFilePath, "rw");
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
	     //Seek to position
	     raf.seek((resumableChunkNumber - 1) * (long)info.resumableChunkSize);

	        //Save to file
	        InputStream is = request.getInputStream();
	        long readed = 0;
	        long content_length = request.getContentLength();
	        byte[] bytes = new byte[1024 * 100];
	        while(readed < content_length) {
	            int r = is.read(bytes);
	            if (r < 0)  {
	                break;
	            }
	            raf.write(bytes, 0, r);
	            readed += r;
	        }
	        raf.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	        
	        
	      //Mark as uploaded.
	        info.uploadedChunks.add(new ResumableInfo.ResumableChunkNumber(resumableChunkNumber));
	        if (info.checkIfUploadFinished()) { //Check if all chunks uploaded, and change filename
	            ResumableInfoStorage.getInstance().remove(info);
	            try {
					response.getWriter().print("All finished.");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        } else {
	            try {
					response.getWriter().print("Upload");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	}
	
	 private int getResumableChunkNumber(HttpServletRequest request) {
	        return HttpUtils.toInt(request.getParameter("resumableChunkNumber"), -1);
	 }
	 
	 private ResumableInfo getResumableInfo(HttpServletRequest request) throws ServletException {
	        String base_dir = UPLOAD_DIR;

	        int resumableChunkSize          = HttpUtils.toInt(request.getParameter("resumableChunkSize"), -1);
	        long resumableTotalSize         = HttpUtils.toLong(request.getParameter("resumableTotalSize"), -1);
	        String resumableIdentifier      = request.getParameter("resumableIdentifier");
	        String resumableFilename        = request.getParameter("resumableFilename");
	        String resumableRelativePath    = request.getParameter("resumableRelativePath");
	        //Here we add a ".temp" to every upload file to indicate NON-FINISHED
	        new File(base_dir).mkdir();
	        String resumableFilePath  = new File(base_dir, resumableFilename).getAbsolutePath() + ".temp";

	        ResumableInfoStorage storage = ResumableInfoStorage.getInstance();

	        ResumableInfo info = storage.get(resumableChunkSize, resumableTotalSize,
	                resumableIdentifier, resumableFilename, resumableRelativePath, resumableFilePath);
	        if (!info.vaild())         {
	            storage.remove(info);
	            throw new ServletException("Invalid request params.");
	        }
	        return info;
	    }

}
