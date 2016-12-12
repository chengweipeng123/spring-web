package controller;

import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by chengseas on 2016/12/12.
 */
@Controller
public class UploadFileController {

    @RequestMapping("/upload-file")
    public String uploadFile(){
        return "upload-file.htm";
    }

    @RequestMapping("/upload-files")
    public String uploadFiles(){
        return "upload-files.htm";
    }

    @RequestMapping("/upload")
    @ResponseBody
    public String upload(@RequestParam("file")MultipartFile file) throws IOException {
        String path = "/path/" + file.getOriginalFilename(); //本地路径
        FileOutputStream out = new FileOutputStream(path);
        FileCopyUtils.copy(file.getInputStream(), out);
        out.flush();
        out.close();
        return "Success";
    }

    @RequestMapping("/upload-multi")
    @ResponseBody
    public String uploadMultipleFiles(@RequestParam("file") MultipartFile[] files) throws IOException {
        for (int i = 0; i < files.length; ++i) {
            MultipartFile file = files[i];

            if (!file.isEmpty()) {
                FileOutputStream out = new FileOutputStream("/path/" + file.getOriginalFilename());
                FileCopyUtils.copy(file.getInputStream(), out);
                out.flush();
                out.close();
            }
        }

        return "Success";
    }

}
