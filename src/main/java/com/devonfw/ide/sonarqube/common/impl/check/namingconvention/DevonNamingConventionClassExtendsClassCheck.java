package com.devonfw.ide.sonarqube.common.impl.check.namingconvention;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sonar.java.ast.parser.JavaParser;
import org.sonar.java.model.JavaTree;
import org.sonar.java.model.ModifiersUtils;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.CompilationUnitTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.ListTree;
import org.sonar.plugins.java.api.tree.Modifier;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.Tree.Kind;
import org.sonar.plugins.java.api.tree.TypeTree;

import com.sonar.sslr.api.typed.ActionParser;

public abstract class DevonNamingConventionClassExtendsClassCheck implements JavaFileScanner {

  private JavaFileScannerContext context;

  final protected String extendedSuperClass;

  final protected String classSuffixRegEx;

  final protected Boolean isAbstract;

  protected String className;

  protected List<String> interfacesToImplement = null;

  public DevonNamingConventionClassExtendsClassCheck(String pExtendedSuperClass, String pClassSuffixRegEx,
      Boolean pIsAbstract) {

    this.extendedSuperClass = pExtendedSuperClass;
    this.classSuffixRegEx = pClassSuffixRegEx;
    this.isAbstract = pIsAbstract;
  }

  public DevonNamingConventionClassExtendsClassCheck(String pExtendedSuperClass, String pClassSuffixRegEx) {

    this.extendedSuperClass = pExtendedSuperClass;
    this.classSuffixRegEx = pClassSuffixRegEx;
    this.isAbstract = null;
  }

  @Override
  public void scanFile(JavaFileScannerContext context) {

    this.context = context;

    File file = context.getFile();

    CompilationUnitTree parsedTree = parseJavaFile(file.getPath());

    List<Tree> types = parsedTree.types();
    ClassTree tree = getTreeInstance(types);
    Kind kind = tree.kind();

    boolean isClass = kind.equals(Tree.Kind.CLASS);
    if (!isClass)
      return;

    TypeTree superClass = tree.superClass();

    LinkedList<String> superInterfacesNames = new LinkedList<String>();
    ListTree<TypeTree> superInterfaces = tree.superInterfaces();

    IdentifierTree simpleName = tree.simpleName();
    String className = simpleName.name();

    this.className = className;

    init();

    for (Iterator<TypeTree> iterator = superInterfaces.iterator(); iterator.hasNext();) {
      TypeTree typeTree = iterator.next();
      superInterfacesNames.add(typeTree.toString());
    }

    String superClassName = null;
    if (superClass != null) {
      superClassName = superClass.toString();
    }

    if (superClassName != null) {

      if (superClassName.equals(this.extendedSuperClass)) {

        Pattern pattern = Pattern.compile(this.classSuffixRegEx);
        Matcher matcher = pattern.matcher(className);
        boolean endsWith = matcher.find();

        if (!endsWith) {
          this.context.addIssueOnFile(this, "Classes inheriting from " + this.extendedSuperClass + " should have "
              + this.classSuffixRegEx + " as prefix");
          return;
        }

        if (this.interfacesToImplement != null) {
          boolean contains = superInterfacesNames.containsAll(this.interfacesToImplement);

          if (!contains) {
            this.context.addIssueOnFile(this, "Classes extending " + this.extendedSuperClass
                + " should implement an interface with the same name except the suffix");
            return;

          }
        }
      } else if (Pattern.compile(this.classSuffixRegEx).matcher(superClassName).find()) {

        if (!Pattern.compile(this.classSuffixRegEx).matcher(className).find()) {
          this.context.addIssueOnFile(this, "If a superclass has " + this.classSuffixRegEx
              + " as prefix, then the subclass should also have " + this.classSuffixRegEx + " as prefix.");
          return;

        }
      }
      boolean isAbstract = isAbstract(tree);

      if (this.isAbstract != null)
        if (this.isAbstract) {
          if (!isAbstract) {

            this.context.addIssueOnFile(this,
                "Classes inheriting from " + this.extendedSuperClass + " should be abstract");
            return;
          }
        } else {
          if (!this.isAbstract) {
            if (isAbstract) {
              this.context.addIssueOnFile(this,
                  "Classes inheriting from " + this.extendedSuperClass + " should not be abstract");
              return;
            }
          }

        }

    }
  }

  protected void init() {

    return;
  }

  private static CompilationUnitTree parseJavaFile(String path) {

    ActionParser<Tree> parser = JavaParser.createParser();

    File input = new File(path);

    Tree javaFileTree = parser.parse(input);

    CompilationUnitTree parsedTree = new JavaTree.CompilationUnitTreeImpl(null, new ArrayList<>(), new ArrayList<>(),
        null);

    parsedTree = (CompilationUnitTree) javaFileTree;

    return parsedTree;
  }

  private static ClassTree getTreeInstance(List<Tree> types) {

    for (Iterator iterator = types.iterator(); iterator.hasNext();) {
      Tree tree = (Tree) iterator.next();
      if (tree instanceof ClassTree) {
        return (ClassTree) tree;
      }
    }
    return null;
  }

  private static boolean isAbstract(ClassTree tree) {

    return ModifiersUtils.hasModifier(tree.modifiers(), Modifier.ABSTRACT);
  }

}
