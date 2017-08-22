package tech.amath0312.fileupload.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import tech.amath0312.fileupload.entity.ThumbFile;

@RestController
@RequestMapping("/files")
public class FileController {

	@RequestMapping(value = "", method = RequestMethod.POST)
	public String upload(@RequestParam("file") MultipartFile file) throws Exception {
		if (file.isEmpty()) {
			throw new Exception("文件不能为空");
		}
		if (file.getOriginalFilename().lastIndexOf(".") < 0) {
			throw new Exception("文件类型错误");
		}

		String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);

		File destFile = new File(System.currentTimeMillis() + "." + suffix).getAbsoluteFile();
		if (!destFile.getParentFile().exists()) {
			destFile.getParentFile().mkdirs();
		}
		file.transferTo(destFile);
		if (!destFile.exists()) {
			return "save to " + destFile.getAbsolutePath() + " error";
		} else {
			return destFile.getAbsolutePath();
		}
	}

	@RequestMapping(value = "/thumbs", method = RequestMethod.POST)
	public String uploadThumbs(@RequestBody ThumbFile thumbFile) throws Exception {
//		@RequestParam("name") String name, @RequestParam("content") String content
		String name = thumbFile.getName();
		String content = thumbFile.getContent();
		System.out.println("name=" + name);
		System.out.println("content=" + content);
		String prefix = "data:";
		if (!content.startsWith(prefix)) {
			throw new Exception("invalid data");
		}

		int endOfType = content.indexOf(';');
		if (endOfType < 0) {
			throw new Exception("invalid data");
		}

		String imageType = content.substring(prefix.length(), endOfType).split("/")[1];
		String baseContent = content.substring(content.indexOf(",", endOfType + 1) + 1);
		baseContent = baseContent.replaceAll("\\s+", "");
		System.out.println(baseContent);
		byte[] data = Base64.getDecoder().decode(baseContent);

		File destFile = new File(System.currentTimeMillis() + "." + imageType).getAbsoluteFile();
		if (!destFile.getParentFile().exists()) {
			destFile.getParentFile().mkdirs();
		}
		FileOutputStream fos = new FileOutputStream(destFile);
		int idx = 0;
		int buf = 4096;
		while (idx < data.length) {
			int left = data.length - idx;
			if (left > buf) {
				left = buf;
			}
			fos.write(data, idx, left);
			fos.flush();
			idx += left;
		}
		fos.close();

		return "ok";
	}

}
