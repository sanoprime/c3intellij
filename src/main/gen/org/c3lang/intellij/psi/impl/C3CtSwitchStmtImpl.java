// This is a generated file. Not intended for manual editing.
package org.c3lang.intellij.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.c3lang.intellij.psi.C3Types.*;
import org.c3lang.intellij.psi.*;

public class C3CtSwitchStmtImpl extends C3PsiElementImpl implements C3CtSwitchStmt {

  public C3CtSwitchStmtImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitCtSwitchStmt(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public C3CtSwitch getCtSwitch() {
    return findNotNullChildByClass(C3CtSwitch.class);
  }

  @Override
  @Nullable
  public C3CtSwitchBody getCtSwitchBody() {
    return findChildByClass(C3CtSwitchBody.class);
  }

}
