/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Nick
 */
public class StringUtil {

    public static String materialToReadable(String name) {
        return Arrays.stream(name.replaceAll("_", " ").split(" ")).map(s -> s.toLowerCase()).map(s -> StringUtils.capitalize(s)).collect(Collectors.joining(" "));
    }
}
