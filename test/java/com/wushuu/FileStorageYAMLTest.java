package com.wushuu;

import java.util.Map;
import java.util.List;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Callback;

public class FileStorageYAMLTest {

    public static class Matrix {
        private int rows;
        private int cols;
        private String dt;
        private List<Float> data;

        public Matrix() {
        }

        public int getRows() {
                return rows;
        }

        public void setRows(int rows) {
                this.rows = rows;
        }

        public int getCols() {
                return cols;
        }

        public void setCols(int cols) {
                this.cols = cols;
        }

        public List<Float> getData() {
                return data;
        }

        public void setData(List<Float> data) {
                this.data = data;
        }

        public String getDt() {
                return dt;
        }

        public void setDt(String dt) {
                this.dt = dt;
        }

    }

    public static class MatrixConstructor extends Constructor {
        protected Class<?> getClassForName(String name) throws ClassNotFoundException 
        {
            System.out.println("***" + name);
            if ("opencv-matrix".equals(name)) {
                return Matrix.class;
            }
            return super.getClassForName(name);
        }
    };

    public interface TestLibrary extends Library {
        TestLibrary INSTANCE = (TestLibrary) Native.loadLibrary("wsnativetest", TestLibrary.class);

        interface test_cb_t extends Callback {
                void invoke(String yaml);
        }

        void set_cb(test_cb_t cb);
        void test_cb();
    }

    public static void main(String[] args) {
        System.out.println("enter main");
        TestLibrary.test_cb_t cb = new TestLibrary.test_cb_t() {
            public void invoke(String s) {
                System.out.println("=========YAML===========");
                System.out.println(s);
                System.out.println("=========YAML===========");
                Map m = (Map)new Yaml(new MatrixConstructor()).load(s.substring(10));
                System.out.println("*********MAP***********");
                System.out.println(m);
                System.out.println("*********MAP***********");
            }
        };
        TestLibrary.INSTANCE.set_cb(cb);
        TestLibrary.INSTANCE.test_cb();
        System.out.println("leave main");
    }
}
