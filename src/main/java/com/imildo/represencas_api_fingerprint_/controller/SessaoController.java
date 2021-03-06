package com.imildo.represencas_api_fingerprint_.controller;

import com.imildo.represencas_api_fingerprint_.model.Estudantes;
import com.imildo.represencas_api_fingerprint_.repository.EstudanteRepository;
import com.zkteco.biometric.FingerprintSensorErrorCode;
import com.zkteco.biometric.FingerprintSensorEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;

/**
 * @author Imildo Sitoe
 */
@SuppressWarnings("ALL")
@RestController
@RequestMapping(path = "/api")
public class SessaoController {

    @Autowired
    private EstudanteRepository er;

    private static final long serialVersionUID = 1L;
    int fpWidth = 0;
    int fpHeight = 0;
    private byte[] lastRegTemp = new byte[2048];
    private int cbRegTemp = 0;
    private byte[][] regtemparray = new byte[3][2048];
    private boolean bRegister = false;
    private boolean bIdentify = true;
    private int iFid = 1;
    private int nFakeFunOn = 1;
    static final int enroll_cnt = 3;
    private int enroll_idx = 0;
    private byte[] imgbuf = null;
    private byte[] template = new byte[2048];
    private int[] templateLen = new int[1];
    private boolean mbStop = true;
    private long mhDevice = 0;
    private long mhDB = 0;
    private WorkThread workThread = null;
    private String textLabel = "";
    private boolean checkStudent = false;
    private int idEstudanteG = -1;
    private int progressG = -1;
    private boolean isMarked;
    private boolean isClicked;
    private boolean isEnrolled2;
    private boolean isEnrolled1;
    private boolean isEnrolled0;
    private Integer id_alocacaoG;

    Connection con = null;
    PreparedStatement pst = null;
    Statement st = null;
    ResultSet rs = null;
    String driver = "org.gjt.mm.mysql.Driver";
    String url = "jdbc:mysql://localhost:3306/represencas_api";
    String user = "root";
    String pass = "";


    @GetMapping("/start_session/{id_alocacao}")
    @ResponseBody
    public String startSession(@PathVariable Integer id_alocacao) {
        if (0 != mhDevice) {
            return "Please close device first!\n";
        }
        int ret = FingerprintSensorErrorCode.ZKFP_ERR_OK;
        //Initialize
        cbRegTemp = 0;
        bRegister = false;
        bIdentify = false;
        iFid = 1;
        enroll_idx = 0;
        id_alocacaoG = id_alocacao;
        if (FingerprintSensorErrorCode.ZKFP_ERR_OK != FingerprintSensorEx.Init()) {
            return "Init failed!\n";
        }
        ret = FingerprintSensorEx.GetDeviceCount();
        if (ret < 0) {
            String text = "No devices connected!\n";
            FreeSensor();
            return text;
        }
        if (0 == (mhDevice = FingerprintSensorEx.OpenDevice(0))) {
            String text1 = "Open device fail, ret = " + ret + "!\n";
            FreeSensor();
            return text1;
        }
        if (0 == (mhDB = FingerprintSensorEx.DBInit())) {
            String text2 = "Init DB fail, ret = " + ret + "!\n";
            FreeSensor();
            return text2;
        }

        int nFmt = 0;
        nFmt = 1;

        FingerprintSensorEx.DBSetParameter(mhDB, 5010, nFmt);

        byte[] paramValue = new byte[4];
        int[] size = new int[1];

        size[0] = 4;
        FingerprintSensorEx.GetParameters(mhDevice, 1, paramValue, size);
        fpWidth = byteArrayToInt(paramValue);
        size[0] = 4;
        FingerprintSensorEx.GetParameters(mhDevice, 2, paramValue, size);
        fpHeight = byteArrayToInt(paramValue);

        imgbuf = new byte[fpWidth * fpHeight];
        mbStop = false;
        workThread = new WorkThread();
        workThread.start();

        return "Open succ! Finger Image Width:" + fpWidth + ",Height:" + fpHeight + "\n";
    }


    @GetMapping("/close_session")
    @ResponseBody
    public String closeSession() {
        FreeSensor();
        checkStudent = false;
        return "Close succ!\n";
    }


    @GetMapping("/check_student")
    @ResponseBody
    public Integer checkStudent() {
        bRegister = false;
        if (0 == mhDevice) {
            System.out.println("Please Open device first!\n");
        }
        checkStudent = true;

        isMarked = true;
        isClicked = true;

        int times = 1;
        for (int i = 0; i < times; i++) {

            if (isMarked && isClicked) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                times++;
            } else if (!isClicked) {
                return 0;
            }
        }

        return idEstudanteG;
    }


    @GetMapping("/break_marking")
    @ResponseBody
    public void breakMarking() {
        isClicked = false;
    }


    @GetMapping("/enroll_student")
    @ResponseBody
    public String enrollStudent() {
        String text = "";
        checkStudent = false;
        if (0 == mhDevice) {
            return "Please Open device first!\n";
        }
        if (!bRegister) {
            enroll_idx = 0;
            bRegister = true;
            text = "Place your finger 3 times!\n";
        }
        return text;
    }


    @GetMapping("/get_enrollment_progress_2")
    @ResponseBody
    public Integer getEnrollmentProgress2() {
        isEnrolled2 = true;
        while (isEnrolled2) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return progressG;
    }


    @GetMapping("/get_enrollment_progress_1")
    @ResponseBody
    public Integer getEnrollmentProgress1() {
        isEnrolled1 = true;
        while (isEnrolled1) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return progressG;
    }


    @GetMapping("/get_enrollment_progress_0")
    @ResponseBody
    public Integer getEnrollmentProgress0() {
        isEnrolled0 = true;
        while (isEnrolled0) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return progressG;
    }


    @PatchMapping("save_template/{id_estudante}")
    @ResponseBody
    public String saveTemplate(@PathVariable Integer id_estudante) {
        Optional<Estudantes> optional = er.findById(id_estudante);
        Estudantes existing = optional.get();

        File file = new File("C:\\Represencas\\pressed_fingerprint_templates\\fingerprint.bmp");
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            for (int readNum; (readNum = fis.read(buf)) != -1; ) {
                bos.write(buf, 0, readNum);
            }
            byte[] compressedImg = bos.toByteArray();

            existing.setIs_finger_registered("1");
            existing.setFinger_template(compressedImg);
            er.save(existing);
        } catch (IOException e) {
            System.err.println(e.toString());
        }

        return "Saved Successfuly!";
    }


    private void FreeSensor() {
        mbStop = true;
        try {        //wait for thread stopping
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (0 != mhDB) {
            FingerprintSensorEx.DBFree(mhDB);
            mhDB = 0;
        }
        if (0 != mhDevice) {
            FingerprintSensorEx.CloseDevice(mhDevice);
            mhDevice = 0;
        }
        FingerprintSensorEx.Terminate();
    }


    private static void writeBitmap(byte[] imageBuf, int nWidth, int nHeight, String path) throws IOException {
        java.io.FileOutputStream fos = new java.io.FileOutputStream(path);
        java.io.DataOutputStream dos = new java.io.DataOutputStream(fos);

        int w = (((nWidth + 3) / 4) * 4);
        int bfType = 0x424d;
        int bfSize = 54 + 1024 + w * nHeight;
        int bfReserved1 = 0;
        int bfReserved2 = 0;
        int bfOffBits = 54 + 1024;

        dos.writeShort(bfType);
        dos.write(changeByte(bfSize), 0, 4);
        dos.write(changeByte(bfReserved1), 0, 2);
        dos.write(changeByte(bfReserved2), 0, 2);
        dos.write(changeByte(bfOffBits), 0, 4);

        int biSize = 40;// ??????????????????????????????14-17?????????
        int biWidth = nWidth;// ???????????????18-21?????????
        int biHeight = nHeight;// ???????????????22-25?????????
        int biPlanes = 1; // ?????????????????????????????????1???26-27?????????
        int biBitcount = 8;// ??????????????????????????????28-29?????????????????????1??????????????????4??????16?????????8??????256????????????24???????????????????????????
        int biCompression = 0;// ??????????????????????????????0??????????????????30-33????????????1???BI_RLEB??????????????????2???BI_RLE4????????????????????????
        int biSizeImage = w * nHeight;// ?????????????????????????????????????????????????????????????????????34-37?????????
        int biXPelsPerMeter = 0;// ??????????????????????????????????????????38-41????????????????????????????????????
        int biYPelsPerMeter = 0;// ??????????????????????????????????????????42-45????????????????????????????????????
        int biClrUsed = 0;// ????????????????????????????????????????????????46-49?????????????????????0??????????????????????????????
        int biClrImportant = 0;// ???????????????????????????????????????(50-53??????)????????????0???????????????????????????

        dos.write(changeByte(biSize), 0, 4);// ????????????????????????????????????
        dos.write(changeByte(biWidth), 0, 4);// ??????????????????
        dos.write(changeByte(biHeight), 0, 4);// ??????????????????
        dos.write(changeByte(biPlanes), 0, 2);// ?????????????????????????????????
        dos.write(changeByte(biBitcount), 0, 2);// ????????????????????????????????????
        dos.write(changeByte(biCompression), 0, 4);// ???????????????????????????
        dos.write(changeByte(biSizeImage), 0, 4);// ???????????????????????????
        dos.write(changeByte(biXPelsPerMeter), 0, 4);// ??????????????????????????????
        dos.write(changeByte(biYPelsPerMeter), 0, 4);// ??????????????????????????????
        dos.write(changeByte(biClrUsed), 0, 4);// ?????????????????????????????????
        dos.write(changeByte(biClrImportant), 0, 4);// ?????????????????????????????????????????????

        for (int i = 0; i < 256; i++) {
            dos.writeByte(i);
            dos.writeByte(i);
            dos.writeByte(i);
            dos.writeByte(0);
        }

        byte[] filter = null;
        if (w > nWidth) {
            filter = new byte[w - nWidth];
        }

        for (int i = 0; i < nHeight; i++) {
            dos.write(imageBuf, (nHeight - 1 - i) * nWidth, nWidth);
            if (w > nWidth)
                dos.write(filter, 0, w - nWidth);
        }
        dos.flush();
        dos.close();
        fos.close();
    }


    private static byte[] changeByte(int data) {
        return intToByteArray(data);
    }


    private static byte[] intToByteArray(final int number) {
        byte[] abyte = new byte[4];
        abyte[0] = (byte) (0xff & number);
        abyte[1] = (byte) ((0xff00 & number) >> 8);
        abyte[2] = (byte) ((0xff0000 & number) >> 16);
        abyte[3] = (byte) ((0xff000000 & number) >> 24);
        return abyte;
    }


    private static int byteArrayToInt(byte[] bytes) {
        int number = bytes[0] & 0xFF;
        number |= ((bytes[1] << 8) & 0xFF00);
        number |= ((bytes[2] << 16) & 0xFF0000);
        number |= ((bytes[3] << 24) & 0xFF000000);
        return number;
    }


    private class WorkThread extends Thread {
        @Override
        public void run() {
            super.run();
            int ret = 0;
            while (!mbStop) {
                templateLen[0] = 2048;
                if (0 == (ret = FingerprintSensorEx.AcquireFingerprint(mhDevice, imgbuf, template, templateLen))) {
                    if (nFakeFunOn == 1) {
                        byte[] paramValue = new byte[4];
                        int[] size = new int[1];
                        size[0] = 4;
                        int nFakeStatus = 0;
                        //GetFakeStatus
                        ret = FingerprintSensorEx.GetParameters(mhDevice, 2004, paramValue, size);
                        nFakeStatus = byteArrayToInt(paramValue);
                        System.out.println("ret = " + ret + ",nFakeStatus=" + nFakeStatus);
                        if (0 == ret && (byte) (nFakeStatus & 31) != 31) {
                            textLabel = "Is a fake finger?\n";
                            return;
                        }
                    }
                    OnCatpureOK(imgbuf);
                    OnExtractOK(template, templateLen[0]);
                    getTemplate();

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }


    private void OnCatpureOK(byte[] imgBuf) {
        try {
            writeBitmap(imgBuf, fpWidth, fpHeight, "C:\\Represencas\\pressed_fingerprint_templates\\fingerprint.bmp");
            //writeBitmap(imgBuf, fpWidth, fpHeight, "C:\\Represencas\\pressed_fingerprint_templates\\fingerprint.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void OnExtractOK(byte[] template, int len) {
        if (bRegister) {
            int[] fid = new int[1];
            int[] score = new int[1];
            int ret = FingerprintSensorEx.DBIdentify(mhDB, template, fid, score);
            if (ret == 0) {
                System.out.println("the finger already enroll by " + fid[0] + ",cancel enroll\n");
                bRegister = false;
                enroll_idx = 0;
                progressG = 200;
                return;
            }
            if (enroll_idx > 0 && FingerprintSensorEx.DBMatch(mhDB, regtemparray[enroll_idx - 1], template) <= 0) {
                System.out.println("please press the same finger 3 times for the enrollment\n");
                progressG = 400;
                return;
            }
            System.arraycopy(template, 0, regtemparray[enroll_idx], 0, 2048);
            enroll_idx++;
            if (enroll_idx == 3) {
                int[] _retLen = new int[1];
                _retLen[0] = 2048;
                byte[] regTemp = new byte[_retLen[0]];

                if (0 == (ret = FingerprintSensorEx.DBMerge(mhDB, regtemparray[0], regtemparray[1], regtemparray[2], regTemp, _retLen)) &&
                        0 == (ret = FingerprintSensorEx.DBAdd(mhDB, iFid, regTemp))) {
                    iFid++;
                    cbRegTemp = _retLen[0];
                    System.arraycopy(regTemp, 0, lastRegTemp, 0, cbRegTemp);
                    System.out.println("enroll succ\n");
                    isEnrolled0 = false;

                    //Ler o Bitmap do source e persistir na BD
                } else {
                    System.out.println("enroll fail, error code=" + ret + "\n");
                }
                bRegister = false;
            } else {
                System.out.println("You need to press the " + (3 - enroll_idx) + " times fingerprint\n");

                if ((3 - enroll_idx) == 2) {
                    isEnrolled2 = false;
                } else if ((3 - enroll_idx) == 1) {
                    isEnrolled1 = false;
                }
            }
        }
    }


    private void getTemplate() {
        Integer id_estudante = -1;

        if (checkStudent) {
            LinkedList<Estudantes> estudantes = er.findAll(id_alocacaoG);

            for (int j = 0; j < estudantes.size(); j++) {
                // PRESSED FINGER
                String path0 = "C:\\Represencas\\pressed_fingerprint_templates\\fingerprint.bmp";
                byte[] fpTemplate0 = new byte[2048];
                int[] sizeFPTemp0 = new int[1];
                sizeFPTemp0[0] = 2048;
                int ret0 = FingerprintSensorEx.ExtractFromImage(mhDB, path0, 500, fpTemplate0, sizeFPTemp0);
                if (0 == ret0) {
                    ret0 = FingerprintSensorEx.DBAdd(mhDB, iFid, fpTemplate0);
                    if (0 == ret0) {
                        iFid++;
                        cbRegTemp = sizeFPTemp0[0];
                        System.arraycopy(fpTemplate0, 0, lastRegTemp, 0, cbRegTemp);
                    } else {
                        System.out.println("DBAdd fail, ret=" + ret0 + "\n");
                    }
                } else {
                    System.out.println("ExtractFromImage fail, ret=" + ret0 + "\n");
                }

                // BD FINGER
                try {
                    Class.forName(driver);
                    con = DriverManager.getConnection(url, user, pass);
                    st = con.createStatement();
                    rs = st.executeQuery("SELECT finger_template FROM estudantes WHERE id_estudante=" + estudantes.get(j).getId_estudante() + ";");

                    Blob blob = null;
                    if (rs.first()) {
                        blob = rs.getBlob("finger_template");
                        int length = (int) blob.length();
                        byte[] blobAsBytes = blob.getBytes(1, length);

                        BufferedImage image = ImageIO.read(new ByteArrayInputStream(blobAsBytes));
                        ImageIO.write(image, "BMP", new File("C:\\Represencas\\db_fingerprint_templates\\fingerprint.bmp"));
                    }
                    con.close();
                    st.close();
                    rs.close();
                } catch (IOException | SQLException | ClassNotFoundException | NullPointerException ex) {

                }

                String path = "C:\\Represencas\\db_fingerprint_templates\\fingerprint.bmp";
                byte[] fpTemplate = new byte[2048];
                int[] sizeFPTemp = new int[1];
                sizeFPTemp[0] = 2048;
                int ret = FingerprintSensorEx.ExtractFromImage(mhDB, path, 500, fpTemplate, sizeFPTemp);

                if (0 == ret) {

                    ret = FingerprintSensorEx.DBMatch(mhDB, lastRegTemp, fpTemplate);

                    if (ret > 0) {
                        id_estudante = estudantes.get(j).getId_estudante();
                        System.out.println("Verify succ, score=" + ret + "\n");
                    } else {
                        System.out.println("Verify fail, ret=" + ret + "\n");
                    }
                } else {
                    System.out.println("ExtractFromImage fail, ret=" + ret + "\n");
                }

                if (id_estudante > 0) {
                    System.out.println("ENCONTRADO! \n Id do Estudante: " + id_estudante + "\n");
                    break;
                }
            }
        }

        idEstudanteG = id_estudante;
        isMarked = false;
        checkStudent = false;
    }
}
