const postId = document.getElementById("postId").value;
const postOwnerId = document.getElementById("postOwnerId").value;
const accessToken = localStorage.getItem("accessToken");
const currentUserId = localStorage.getItem("userId");

// 댓글 로드
window.onload = () => {
  fetch(`/api/comments/post/${postId}`)
    .then(res => res.json())
    .then(data => renderComments(data));
};

// 댓글 렌더링
function renderComments(comments) {
  const container = document.getElementById("comments-container");
  container.innerHTML = "";

  comments.forEach(comment => {
    const commentEl = buildCommentElement(comment);
    container.appendChild(commentEl);
  });
}

// 댓글 DOM 구성
function buildCommentElement(comment, isChild = false) {
  const div = document.createElement("div");
  div.style.marginLeft = isChild ? "30px" : "0";
  div.style.borderBottom = "1px solid #ccc";
  div.style.padding = "10px 0";

  const info = document.createElement("p");
  info.innerHTML = `<strong>${comment.nickname}</strong> | ${comment.createAt}`;
  div.appendChild(info);

  const content = document.createElement("p");
  content.textContent = comment.content;
  div.appendChild(content);

  // 버튼들
  if (currentUserId && Number(currentUserId) === comment.userId) {
    const editBtn = document.createElement("button");
    editBtn.textContent = "수정";
    editBtn.onclick = () => {
      const newContent = prompt("수정할 내용을 입력하세요", comment.content);
      if (newContent) updateComment(comment.commentId, newContent);
    };
    div.appendChild(editBtn);

    const deleteBtn = document.createElement("button");
    deleteBtn.textContent = "삭제";
    deleteBtn.onclick = () => deleteComment(comment.commentId);
    div.appendChild(deleteBtn);
  }

  // 채택 버튼 (게시글 작성자만 가능 & 채택 안 된 경우만)
  if (postOwnerId === currentUserId && !comment.answer) {
    const adoptBtn = document.createElement("button");
    adoptBtn.textContent = "채택";
    adoptBtn.onclick = () => adoptComment(comment.commentId);
    div.appendChild(adoptBtn);
  }

  // 대댓글
  const replyBtn = document.createElement("button");
  replyBtn.textContent = "답글";
  replyBtn.onclick = () => {
    const replyContent = prompt("대댓글을 입력하세요");
    if (replyContent) createComment(replyContent, comment.commentId);
  };
  div.appendChild(replyBtn);

  // 채택 표시
  if (comment.answer) {
    const adopted = document.createElement("span");
    adopted.textContent = " ✅ 채택됨";
    adopted.style.color = "green";
    div.appendChild(adopted);
  }

  // 자식 댓글 재귀 호출
  if (comment.children && comment.children.length > 0) {
    comment.children.forEach(child => {
      const childEl = buildCommentElement(child, true);
      div.appendChild(childEl);
    });
  }

  return div;
}

// 댓글 작성
function createComment(content = null, parentId = null) {
  const textarea = document.getElementById("newCommentContent");
  const commentContent = content || textarea.value.trim();

  if (!commentContent) return alert("댓글 내용을 입력하세요");

  fetch("/api/comments", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: "Bearer " + accessToken,
    },
    body: JSON.stringify({
      content: commentContent,
      postId: postId,
      userId: currentUserId,
      parentId: parentId,
    }),
  })
    .then(res => {
      if (!res.ok) throw new Error("작성 실패");
      return res.json();
    })
    .then(() => {
      textarea.value = "";
      window.onload(); // 댓글 새로고침
    })
    .catch(() => alert("댓글 작성 실패"));
}

// 댓글 수정
function updateComment(commentId, newContent) {
  fetch(`/api/comments/${commentId}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Authorization: "Bearer " + accessToken,
    },
    body: JSON.stringify({ content: newContent }),
  })
    .then(res => {
      if (!res.ok) throw new Error("수정 실패");
      window.onload();
    })
    .catch(() => alert("댓글 수정 실패"));
}

// 댓글 삭제
function deleteComment(commentId) {
  if (!confirm("댓글을 삭제할까요?")) return;

  fetch(`/api/comments/${commentId}`, {
    method: "DELETE",
    headers: {
      Authorization: "Bearer " + accessToken,
    },
  })
    .then(res => {
      if (!res.ok) throw new Error("삭제 실패");
      window.onload();
    })
    .catch(() => alert("댓글 삭제 실패"));
}

// 댓글 채택
function adoptComment(commentId) {
  if (!confirm("이 댓글을 채택하시겠습니까?")) return;

  fetch(`/api/comments/${commentId}/adopt`, {
    method: "POST",
    headers: {
      Authorization: "Bearer " + accessToken,
    },
  })
    .then(res => {
      if (!res.ok) throw new Error("채택 실패");
      window.onload();
    })
    .catch(() => alert("댓글 채택 실패"));
}
